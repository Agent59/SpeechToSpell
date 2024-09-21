package net.agent59.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.agent59.Main;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads json files ({@link Resource}s) into {@link Gson} representations and merges those that have the same name.
 *
 * <p>Similar to {@link net.minecraft.resource.JsonDataLoader}, but merges files with the same name instead of discarding them.
 * The resulting {@link JsonObject}s have every field of the files with the same name.
 * If files have declared the same field (apart from the priority field),
 * the {@code "priority"} field is used to resolve the conflict.
 * Files that have declared a higher priority (e.g. {@code "priority": 1})
 * override values from other files that have declared a lower priority (e.g. {@code "priority": 0}).
 * If there is a conflict and files have the same priority, the fields of the file that is read first are used.
 *
 * <p>The json files that are read should contain a {@code "priority"} field with an integer as a value.
 * This mod uses {@code 0} as the default value for the builtin datapack.
 *
 * <p>A {@code "deactivate"} field can be used to deactivate a merged file.
 * If the highest priority sets the field to true ({@code "deactivate": true}) the merged file will be ignored.
 *
 * @see net.minecraft.resource.JsonDataLoader
 * @see SinglePreparationResourceReloader
 * @see net.agent59.spell_school.SpellSchoolManager
 * @see net.agent59.spell.SpellManager
 * @see #merge(List, HashMap, Identifier)
 */
public abstract class MergingJsonDataLoader extends SinglePreparationResourceReloader<Map<Identifier, JsonObject>> {
    public static final String DEACTIVATION_FIELD = "deactivate";
    public static final String PRIORITY_FIELD = "priority";
    public static final String ID_FIELD = "id";

    private static final Logger LOGGER = LogManager.getLogger();
    private final Gson gson;
    private final String dataType; // Used as the "directoryName" in the ResourceFinder.

    protected MergingJsonDataLoader(Gson gson, String dataType) {
        this.gson = gson;
        this.dataType = dataType;
    }

    /**
     * @return A map where the keys are the {@link Resource}s' ids and the values are the prepared {@link JsonObject}s.
     * @see #merge(List, HashMap, Identifier) 
     */
    @Override
    protected Map<Identifier, JsonObject> prepare(ResourceManager resourceManager, Profiler profiler) {
        HashMap<Identifier, JsonObject> map = new HashMap<>();
        ResourceFinder resourceFinder = ResourceFinder.json(this.dataType);

        // Key: Name of the field
        // Pair left: The priority with which the value of the field was selected
        // Pair right: The name of the resourcepack the selected value is from
        HashMap<String, Pair<Integer, String>> fieldsPriorities = new HashMap<>();

        resourceFinder.findAllResources(resourceManager).forEach((resourcePath, resources) -> {
            JsonObject mergedJsonObj = this.merge(resources, fieldsPriorities, resourcePath);

            if (mergedJsonObj.has(DEACTIVATION_FIELD) && mergedJsonObj.get(DEACTIVATION_FIELD).getAsBoolean()) {
                LOGGER.info("All resources with path {} have been deactivated with priority {} " +
                                "by resourcepack {} and will not be loaded.",
                        resourcePath.getPath(), fieldsPriorities.get(DEACTIVATION_FIELD).getLeft(),
                        fieldsPriorities.get(DEACTIVATION_FIELD).getRight()
                );
            } else {
                map.put(resourceFinder.toResourceId(resourcePath), mergedJsonObj);
            }
            fieldsPriorities.clear();
        });
        return map;
    }

    /**
     * Merges the given {@link Resource}s (json files) into one {@link JsonObject}.
     *
     * <p>The returned JsonObject has all the fields that are present in the resources.
     * If the resources have fields with the same name (apart from the priority field),
     * the {@code "priority"} field is used to resolve the conflict.
     * In this case the field from the resource with the highest priority will be used.
     *
     * <p>If there is a conflict but multiple resources have the same priority,
     * a warning will be logged and the value of the resource, that was first read, is used.
     *
     * <p>A {@code "priority"} field is required in each of the resources to be able to resolve conflicts.
     *
     * @param resources Contain the information of json files from datapacks.
     * @param fieldsPriorities Used to track with which priority the fields values have been selected.
     *                         Should be empty by default, but is set as a parameter,
     *                         so the hashmaps memory can be reused when calling merge in a loop.
     *                         Does also hold the name of the datapack a fields selected value is from.
     * @param resourcePath The path of the resource (json file) in the form of an Identifier.
     * @return A JsonObject that contains the fields of the given resources,
     * where conflicting fields have been resolved based on priority.
     */
    private JsonObject merge(
            List<Resource> resources,
            HashMap<String, Pair<Integer, String>> fieldsPriorities,
            Identifier resourcePath
    ) {
        JsonObject mergedJsonObj = new JsonObject();

        for (Resource resource : resources) {
            String packName = resource.getResourcePackName();
            try (Reader reader = resource.getReader()) { // Might throw an IOException.
                // Deserializes the resource into a JsonObject.
                // Might throw a JsonParseException.
                JsonObject jsonObj = JsonHelper.deserialize(this.gson, reader, JsonElement.class).getAsJsonObject();

                JsonElement priorityField = jsonObj.get(PRIORITY_FIELD);
                if (priorityField == null) {
                    LOGGER.warn("File {} in Resourcepack {} is missing a priority field and will be skipped.",
                            resourcePath.getPath(), packName);
                    continue;
                }
                // Might throw an UnsupportedOperationException, NumberFormatException or IllegalStateException.
                int currentPriority = priorityField.getAsInt();

                for (Map.Entry<String, JsonElement> jsonField : jsonObj.entrySet()) {
                    String fieldName = jsonField.getKey();
                    // The priority field should not be merged into the JsonObject.
                    if (fieldName.equals(PRIORITY_FIELD)) continue;

                    Pair<Integer, String> selectedFieldsPriority = fieldsPriorities.get(fieldName);

                    // Checks whether the field is already defined.
                    if (selectedFieldsPriority != null) {
                        if (selectedFieldsPriority.getLeft() > currentPriority) continue;
                        if (selectedFieldsPriority.getLeft() == currentPriority) {
                            LOGGER.warn("""
                                            Resourcepack {} and {} both have the "{}" field \
                                            and the same priority in the path {}. \
                                            The "{}" field of resourcepack {} will be ignored.""",
                                    selectedFieldsPriority.getRight(), packName,
                                    fieldName, resourcePath.getPath(), fieldName, packName);
                            continue;
                        }
                    }
                    // If no such field was encountered before or the current resources priority is higher,
                    // the value of the current field is used.
                    fieldsPriorities.put(fieldName, new Pair<>(currentPriority, packName));
                    mergedJsonObj.add(fieldName, jsonField.getValue());
                }
            } catch (IOException | JsonParseException e) {
                LOGGER.error("""
                                Could not parse file {} from resourcepack {}, due to the following error:
                                {}
                                Skipping this entry.""", resourcePath.getPath(), packName, e);
            } catch (UnsupportedOperationException | NumberFormatException | IllegalStateException e) {
                LOGGER.error("""
                                Could not parse file {} from resourcepack {}, due an error with the priority field:
                                {}
                                Skipping this entry.""", resourcePath.getPath(), packName, e);
            }
        }
        return mergedJsonObj;
    }

    /**
     * Adds an {@code "id"} field with the given id to the given {@link JsonObject}.
     * @param jsonObj The JsonObject to which the id is added as a field ({@code "id": <id>}).
     * @param id The id that is added to the JsonObj.
     * @throws JsonParseException If the id could not be parsed to a JsonObject.
     */
    public static void addIdField(JsonObject jsonObj, Identifier id) {
        jsonObj.add(ID_FIELD, Identifier.CODEC.encodeStart(JsonOps.INSTANCE, id)
                .resultOrPartial((errMsg) -> Main.LOGGER.error("""
                        Could not encode id {} when trying to add an "id" field to jsonObject {},\
                        due to the following error:
                        {}
                        """, id, jsonObj, errMsg))
                .orElseThrow(() -> new JsonParseException("Could not encode identifier " + id)));
    }
}