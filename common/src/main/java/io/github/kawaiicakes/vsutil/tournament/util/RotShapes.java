package io.github.kawaiicakes.vsutil.tournament.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RotShapes {
    public interface RotShape {
        RotShape rotate90();

        default RotShape rotate180() {
            return rotate90().rotate90();
        }

        default RotShape rotate270() {
            return rotate180().rotate90();
        }

        RotShape xrotate90();

        default RotShape xrotate180() {
            return xrotate90().xrotate90();
        }

        default RotShape xrotate270() {
            return xrotate180().xrotate90();
        }

        VoxelShape makeMcShape();

        default VoxelShape build() {
            return makeMcShape().optimize();
        }
    }

    public static class DirectionalShape {
        public final VoxelShape north;
        public final VoxelShape east;
        public final VoxelShape south;
        public final VoxelShape west;
        public final VoxelShape up;
        public final VoxelShape down;

        private DirectionalShape(RotShape shape) {
            this.north = shape.build();
            this.east = shape.rotate90().build();
            this.south = shape.rotate180().build();
            this.west = shape.rotate270().build();
            this.up = shape.xrotate90().build();
            this.down = shape.xrotate270().build();
        }

        public VoxelShape get(Direction direction) {
            return switch (direction) {
                case DOWN -> this.down;
                case UP -> this.up;
                case NORTH -> this.north;
                case SOUTH -> this.south;
                case WEST -> this.west;
                case EAST -> this.east;
            };
        }

        public static DirectionalShape south(RotShape shape) {
            return new DirectionalShape(shape.rotate180());
        }
    }

    public static RotShape box(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new Box(x1, y1, z1, x2, y2, z2);
    }

    record Box(double x1, double y1, double z1, double x2, double y2, double z2) implements RotShape {
        @Override
        public RotShape rotate90() {
            return new Box(16 - z1, y1, x1, 16 - z2, y2, x2);
        }

        @Override
        public RotShape xrotate90() {
            return new Box(x1, 16 -z1, y1, x2, 16 -z2, y2);
        }

        @Override
        public VoxelShape makeMcShape() {
            return Shapes.box(
                    Math.min(x1, x2) / 16,
                    Math.min(y1, y2) / 16,
                    Math.min(z1, z2) / 16,
                    Math.max(x1, x2) / 16,
                    Math.max(y1, y2) / 16,
                    Math.max(z1, z2) / 16
            );
        }
    }
}
