package net.agent59.spell.util;

import net.minecraft.util.math.Vec3i;

import java.util.concurrent.ThreadLocalRandom;

public class ShapeUtil {

    /**
     * @param minLength The minimal length of the vector.
     * @param maxLength The maximal length of the vector.
     * @return A vector with a random direction and length between the minLength and maxLength.
     * @see <a href=https://math.stackexchange.com/questions/44689/how-to-find-a-random-axis-or-unit-vector-in-3d>This answer on math.stackexchange.com</a>
     */
    public static Vec3i randomVec3i(int minLength, int maxLength) {
        double theta = ThreadLocalRandom.current().nextDouble(Math.TAU);
        double z = ThreadLocalRandom.current().nextDouble(-1, 1);
        double randomLength = ThreadLocalRandom.current().nextDouble(minLength, maxLength);

        int x = (int) ((Math.sqrt(1 - z * z) * Math.cos(theta)) * randomLength);
        int y = (int) (Math.sqrt(1 - z * z) * Math.cos(theta) * randomLength);
        return new Vec3i(x, y, (int) (z * randomLength));
    }
}
