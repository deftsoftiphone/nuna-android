function Effect() {
    var self = this;

    this.init = function() {

        spawnDevil();

        if (Api.getPlatform().toLowerCase() === "ios") {
            Api.showHint("Voice changer");
            Api.meshfxMsg("shaderVec4", 0, 0, "1 0 0 0");
        } else {
            Api.meshfxMsg("shaderVec4", 0, 0, "0 0 0 0");
        }

        Api.playVideo('frx',true,1);
        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        Api.stopSound("music_demon.ogg");
        self.init();
    };

    this.faceActions = [];
    this.noFaceActions = [];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

function spawnDevil() {

    Api.meshfxMsg("spawn", 0, 0, "!glfx_FACE");
    Api.meshfxMsg("spawn", 1, 0, "devil.bsm2");
    Api.meshfxMsg("spawn", 2, 0, "morph.bsm2");
    Api.meshfxMsg("spawn", 3, 0, "devBG.bsm2");
    Api.meshfxMsg("spawn", 4, 0, "tri.bsm2");
    Api.meshfxMsg("spawn", 5, 0, "devFG.bsm2");

    Api.meshfxMsg("tex", 4, 0, "LUT_devil.png");

    Api.playSound("music_demon.ogg", true, 1);
}

configure(new Effect());
