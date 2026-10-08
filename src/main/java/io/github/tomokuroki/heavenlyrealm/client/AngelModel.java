package io.github.tomokuroki.heavenlyrealm.client;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import io.github.tomokuroki.heavenlyrealm.AngelEntity;

public final class AngelModel extends HierarchicalModel<AngelEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public AngelModel(ModelPart root) {
        this.root = root;
        ModelPart core = root.getChild("core");
        this.head = core.getChild("head");
        this.leftArm = core.getChild("left_arm");
        this.rightArm = core.getChild("right_arm");
        this.leftWing = core.getChild("left_wing");
        this.rightWing = core.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition core = root.addOrReplaceChild("core", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        core.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20)
                .addBox(-4.5F, -13.0F, -2.5F, 9.0F, 13.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.ZERO);
        core.addOrReplaceChild("robe", CubeListBuilder.create().texOffs(28, 20)
                .addBox(-6.0F, -8.0F, -3.0F, 12.0F, 8.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.ZERO);
        core.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, -13.0F, 0.0F));
        core.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-5.0F, -0.5F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(-0.15F)),
                PartPose.offset(0.0F, -23.0F, 0.0F));
        core.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 40)
                .addBox(-1.5F, -1.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(6.0F, -12.0F, 0.0F));
        core.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(12, 40).mirror()
                .addBox(-1.5F, -1.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offset(-6.0F, -12.0F, 0.0F));

        core.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(24, 38)
                .addBox(0.0F, -11.0F, 0.0F, 12.0F, 20.0F, 1.0F, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(2.0F, -12.0F, 2.5F, 0.15F, 0.25F, -0.25F));
        core.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(24, 38).mirror()
                .addBox(-12.0F, -11.0F, 0.0F, 12.0F, 20.0F, 1.0F, new CubeDeformation(0.15F)),
                PartPose.offsetAndRotation(-2.0F, -12.0F, 2.5F, 0.15F, -0.25F, 0.25F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(AngelEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD;
        float walk = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        leftArm.xRot = -walk;
        rightArm.xRot = walk;
        float flap = 0.18F + Mth.sin(ageInTicks * 0.16F) * 0.16F;
        leftWing.yRot = flap;
        rightWing.yRot = -flap;
        leftWing.zRot = -0.22F - Mth.sin(ageInTicks * 0.12F) * 0.08F;
        rightWing.zRot = 0.22F + Mth.sin(ageInTicks * 0.12F) * 0.08F;
        root.y = Mth.sin(ageInTicks * 0.08F) * 0.7F;
    }
}
