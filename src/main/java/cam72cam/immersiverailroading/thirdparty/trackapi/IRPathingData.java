package cam72cam.immersiverailroading.thirdparty.trackapi;

import cam72cam.immersiverailroading.track.graph.OffWorldMovementTrack;
import cam72cam.immersiverailroading.track.graph.TrackEdge;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.world.World;
import trackapi.lib.PathingData;

public class IRPathingData extends trackapi.lib.PathingData {
    private Vec3d posCache;
    private double deltaMovement;

    // topo
    private World topoWorld;
    private TrackEdge topoEdge;
    private double topoT;

    public IRPathingData(Vec3d position, double roll) {
        this(position, roll, 0);
    }

    protected IRPathingData(Vec3d position, double roll, double deltaMovement) {
        super(position.internal(), roll);
        this.posCache = position;
        this.deltaMovement = deltaMovement;
    }

    public IRPathingData(World world, Vec3d position, double roll) {
        this(world, position, roll, 0);
    }

    protected IRPathingData(World world, Vec3d position, double roll, double deltaMovement) {
        super(position.internal(), roll);
        this.posCache = position;
        this.deltaMovement = deltaMovement;
        this.topoWorld = world;
    }

    public Vec3d getUMCPos() {
        return posCache;
    }

    public IRPathingData setUMCPos(Vec3d pos) {
        return setUMCPos(pos, false);
    }

    public IRPathingData setUMCPos(Vec3d pos, boolean preserveDeltaMovement) {
        this.posCache = pos;
        super.setPos(pos.internal());
        if (!preserveDeltaMovement) {
            this.deltaMovement = 0;
        }
        return this;
    }

    @Override
    public IRPathingData setRoll(double roll) {
        return wrap(super.setRoll(roll));
    }

    public double getDeltaMovement() {
        return deltaMovement;
    }

    public void advanceTo(IRPathingData next) {
        advanceTo(next.getUMCPos(), next.getRoll());
    }

    public void advanceTo(Vec3d nextPos) {
        advanceTo(nextPos, 0);
    }

    public void advanceTo(Vec3d nextPos, double nextRoll) {
        this.deltaMovement += nextPos.distanceTo(this.posCache);
        this.setPos(nextPos.internal());
        this.setRoll(nextRoll);
    }

    public void copyFrom(IRPathingData other) {
        this.setPos(other.getPos()).setRoll(other.getRoll());
        this.deltaMovement = other.deltaMovement;
    }

    @Override
    public IRPathingData clone() {
        IRPathingData c = new IRPathingData(this.posCache, this.getRoll(), this.deltaMovement);
        c.topoWorld = this.topoWorld;
        c.topoEdge = this.topoEdge;
        c.topoT = this.topoT;
        return c;
    }

    public static IRPathingData wrap(trackapi.lib.PathingData inputData) {
        if (inputData instanceof IRPathingData) {
            return (IRPathingData) inputData;
        } else {
            return new IRPathingData(new Vec3d(inputData.getPos()), inputData.getRoll());
        }
    }

    /**
     * @deprecated Use the UMC variant
     */
    @Deprecated
    @Override
    public trackapi.lib.PathingData setPos(net.minecraft.util.math.Vec3d pos) {
        super.setPos(pos);
        if (this.posCache == null || !this.posCache.internal().equals(pos)) {
            Vec3d newPos = new Vec3d(pos);
            this.deltaMovement += newPos.distanceTo(this.posCache);
            this.posCache = newPos;
        }
        return this;
    }

    public World getTopoWorld() {
        return topoWorld;
    }

    public void setTopoWorld(World world) {
        this.topoWorld = world;
    }

    public TrackEdge getTopoEdge() {
        return topoEdge;
    }

    public double getTopoT() {
        return topoT;
    }

    public void advanceTo(TrackEdge nextEdge, double nextT, double nextRoll) {
        this.topoEdge = nextEdge;
        this.topoT = nextT;
        this.setRoll(nextRoll);
        if (topoWorld != null && nextEdge != null) {
            Vec3d newPos = OffWorldMovementTrack.positionOnEdge(nextEdge, nextT, topoWorld);
            this.setPos(newPos.internal());
        }
    }
}