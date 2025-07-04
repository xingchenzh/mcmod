package com.example.autoaim;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AimHandler {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final double RANGE = 30.0; // 瞄准范围
    private static final double ANGLE_THRESHOLD = 0.02; // 瞄准精度阈值
    private static final float ROTATION_SPEED = 8.0f; // 旋转速度

    @SubscribeEvent
    public void onRenderTick(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        if (!ClientEvents.isAutoAimEnabled() || mc.player == null) return;

        Entity target = findNearestTarget();
        if (target != null) {
            aimAtTarget(target);
        }
    }

    private Entity findNearestTarget() {
        Entity nearest = null;
        double minAngle = Double.MAX_VALUE;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!isValidTarget(entity)) continue;

            double angle = calculateAngleToEntity(entity);
            if (angle < minAngle && angle < Math.toRadians(30)) { // 30度锥形视野
                minAngle = angle;
                nearest = entity;
            }
        }
        return nearest;
    }

    private boolean isValidTarget(Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        if (entity == mc.player) return false;
        if (entity.distanceTo(mc.player) > RANGE) return false;
        
        // 只瞄准敌对生物和玩家（非队友）
        return (entity instanceof Monster) || 
               (entity instanceof Player && ((Player)entity).isAlliedTo(mc.player));
    }

    private double calculateAngleToEntity(Entity target) {
        Vec3 playerPos = mc.player.getEyePosition(1.0f);
        Vec3 targetPos = target.getBoundingBox().getCenter();
        Vec3 direction = targetPos.subtract(playerPos).normalize();
        Vec3 lookVec = mc.player.getLookAngle();
        
        return lookVec.dot(direction);
    }

    private void aimAtTarget(Entity target) {
        Vec3 playerPos = mc.player.getEyePosition(1.0f);
        Vec3 targetPos = target.getBoundingBox().getCenter();
        Vec3 direction = targetPos.subtract(playerPos);
        
        double distX = direction.x;
        double distY = direction.y;
        double distZ = direction.z;
        
        double horizontalDistance = Math.sqrt(distX * distX + distZ * distZ);
        
        float targetYaw = (float) Math.toDegrees(Math.atan2(distZ, distX)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(distY, horizontalDistance));
        
        // 平滑过渡
        float currentYaw = mc.player.getYRot();
        float currentPitch = mc.player.getXRot();
        
        float deltaYaw = normalizeAngle(targetYaw - currentYaw);
        float deltaPitch = normalizeAngle(targetPitch - currentPitch);
        
        if (Math.abs(deltaYaw) > ANGLE_THRESHOLD || Math.abs(deltaPitch) > ANGLE_THRESHOLD) {
            mc.player.setYRot(currentYaw + deltaYaw * ROTATION_SPEED * 0.15f);
            mc.player.setXRot(currentPitch + deltaPitch * ROTATION_SPEED * 0.15f);
        }
    }

    private float normalizeAngle(float angle) {
        angle %= 360.0f;
        if (angle > 180.0f) {
            angle -= 360.0f;
        } else if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }
}
private boolean isValidTarget(Entity entity) {
    // 添加更多目标类型
    return (entity instanceof Monster) ||
           (entity instanceof Player && ((Player)entity).isAlliedTo(mc.player)) ||
           (entity instanceof IronGolem) || // 新增铁傀儡
           (entity instanceof Wolf);        // 新增狼
}