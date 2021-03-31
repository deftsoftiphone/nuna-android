function Effect() {
    var self = this;
    var c = 0.5;
    var sec = 0;
    var lastFrame;
    this.play = function() {
        var now = (new Date()).getTime();
        sec += (now - lastFrame)/1000;
        Api.meshfxMsg("shaderVec4", 0, 0, String(sec));
        //Api.showHint(String(sec));
        lastFrame = now;
    }

    function frac(f) {
        return f % 1;
    }

    this.init = function() {
        Api.meshfxMsg("spawn", 3, 0, "!glfx_FACE");
        Api.meshfxMsg("spawn", 2, 0, "BeautyFaceSP_Optimased.bsm2");
        Api.meshfxMsg("spawn", 1, 0, "BurningMan.bsm2");
        Api.meshfxMsg("spawn", 0, 0, "tri.bsm2");
        lastFrame = (new Date()).getTime();

        Api.playVideo("foreground",true,1);
        Api.playSound("music.ogg", true, 1);

        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        Api.stopSound("music.ogg");
        self.init();
    };

    this.faceActions = [self.play];
    this.noFaceActions = [];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

configure(new Effect());