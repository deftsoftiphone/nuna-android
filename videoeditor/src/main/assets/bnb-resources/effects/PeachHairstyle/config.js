function Effect() {
    var self = this;

    this.init = function() {
        Api.meshfxMsg("spawn", 2, 0, "!glfx_FACE");

        Api.meshfxMsg("spawn", 1, 0, "PH_morth.bsm2");

        Api.meshfxMsg("spawn", 0, 0, "PeachHair.bsm2");
        /*
Cube.005
Cube.002
HairFromCurves_02.008
Cube.001
HairFromCurves1.027
HairFromCurves.027
HairFromCurves.025
HairFromCurves.024
HeadHires.005
HairFromCurves_02.060
HairFromCurves_02.059
HairFromCurves_02.058
HairFromCurves_02.057
HairFromCurves_02.056
HairFromCurves_02.055
HairFromCurves_02.054
HairFromCurves_02.053
HairFromCurves_02.052
HairFromCurves_02.051
HairFromCurves1.038
HairFromCurves_02.050
HairFromCurves1.036
HairFromCurves1.035
HairFromCurves1.034
HairFromCurves_02.048
HairFromCurves_02.047
HairFromCurves1.031
HairFromCurves_02.046
Sphere.006
Torus.006
HairFromCurves_02.043
Torus.005
Sphere.005

Armature
Bone_Root       -> Armature
Bone4_L -> Bone_Root
Bone3_L -> Bone4_L
Bone2_L -> Bone3_L
Bone4_L.001     -> Bone_Root
Bone3_L.001     -> Bone4_L.001
Bone2_L.001     -> Bone3_L.001
Bone.001        -> Bone2_L.001
Bone    -> Bone2_L
        */
        Api.meshfxMsg("dynImass", 0, 0, "Armature");
        Api.meshfxMsg("dynImass", 0, 0, "Bone_Root");
        Api.meshfxMsg("dynImass", 0, 0, "Bone4_L");
        Api.meshfxMsg("dynImass", 0, 0, "Bone4_L.001");
        Api.meshfxMsg("dynConstraint", 0, 10, "Bone3_L Armature");
        Api.meshfxMsg("dynConstraint", 0, 10, "Bone3_L.001 Armature");
        Api.meshfxMsg("dynConstraint", 0, 20, "Bone2_L Bone4_L");
        Api.meshfxMsg("dynConstraint", 0, 20, "Bone2_L.001 Bone4_L.001");

        Api.meshfxMsg("dynSphere", 0, 0, "0 -55 0 65");

        Api.meshfxMsg("dynGravity", 0, 0, "0 -2000 0");

        Api.meshfxMsg("dynImass", 0, 0, "Cube.005");
        Api.meshfxMsg("dynImass", 0, 0, "Cube.002");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.008");
        Api.meshfxMsg("dynImass", 0, 0, "Cube.001");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves1.027");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves.027");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves.025");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves.024");
        Api.meshfxMsg("dynImass", 0, 0, "HeadHires.005");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.060");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.059");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.058");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.057");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.056");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.055");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.054");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.053");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.052");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.051");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves1.038");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.050");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves1.036");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves1.035");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves1.034");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.048");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.047");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves1.031");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.046");
        Api.meshfxMsg("dynImass", 0, 0, "Sphere.006");
        Api.meshfxMsg("dynImass", 0, 0, "Torus.006");
        Api.meshfxMsg("dynImass", 0, 0, "HairFromCurves_02.043");
        Api.meshfxMsg("dynImass", 0, 0, "Torus.005");
        Api.meshfxMsg("dynImass", 0, 0, "Sphere.005");

        Api.playSound("Loreal_music_L_Ch.ogg",true,1);
        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        // Api.stopVideo("frx");
        // Api.stopSound("sfx.aac");
        self.init();
    };

    this.faceActions = [];
    this.noFaceActions = [];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

configure(new Effect());