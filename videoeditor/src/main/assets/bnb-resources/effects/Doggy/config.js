function Effect() {
    var self = this;

    this.meshes = [
        { file: "2d.bsm2", anims: [
            { a: "Take 001", t: 4966 },
        ] },
        { file: "glasses.bsm2", anims: [
            { a: "static", t: 0 },
        ] },
        { file: "morph.bsm2", anims: [
            { a: "static", t: 0 },
        ] },
    ];

    this.play = function() {
        var now = (new Date()).getTime();
        for(var i = 0; i < self.meshes.length; i++) {
            if(now > self.meshes[i].endTime) {
                self.meshes[i].animIdx = (self.meshes[i].animIdx + 1)%self.meshes[i].anims.length;
                Api.meshfxMsg("animOnce", i, 0, self.meshes[i].anims[self.meshes[i].animIdx].a);
                self.meshes[i].endTime = now + self.meshes[i].anims[self.meshes[i].animIdx].t;
            }
        }

        // if(isMouthOpen(world.landmarks, world.latents)) {
        //  Api.hideHint();
        // }
    };

    this.init = function() {
        Api.meshfxMsg("spawn", 3, 0, "!glfx_FACE");

        Api.meshfxMsg("spawn", 0, 0, "2d.bsm2");
        // Api.meshfxMsg("animOnce", 0, 0, "Take 001");

        Api.meshfxMsg("spawn", 1, 0, "glasses.bsm2");
        // Api.meshfxMsg("animOnce", 1, 0, "static");

        Api.meshfxMsg("spawn", 2, 0, "morph.bsm2");
        // Api.meshfxMsg("animOnce", 2, 0, "static");

        Api.playVideo("frx", true, 1);           

        for(var i = 0; i < self.meshes.length; i++) {
            self.meshes[i].animIdx = -1;
            self.meshes[i].endTime = 0;
        }

        self.faceActions = [self.play];
        // Enable background audio playback
        //Api.playSound("music.ogg", true, 1);
        // Api.showHint("Open mouth");

        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        //Api.stopSound("music.ogg");
        self.init();
    };

    this.faceActions = [];
    this.noFaceActions = [];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

configure(new Effect());