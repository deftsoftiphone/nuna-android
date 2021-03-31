function Effect() {
    var timer = new Date().getTime();
    var delay = 3000;
    var self = this;
    
    this.hideHint = function() {
        var now = new Date().getTime();

        if (now >= timer + delay) {
                Api.hideHint();
                var index = self.faceActions.indexOf(self.hideHint);
                self.faceActions.splice(index, 1);
        }
    };


    this.init = function() {
        Api.meshfxMsg("spawn", 3, 0, "!glfx_FACE");

        Api.meshfxMsg("spawn", 0, 0, "BeautyFaceSP.bsm2");

        Api.meshfxMsg("spawn", 0, 0, "tri.bsm2");

        Api.meshfxMsg("spawn", 2, 0, "plane.bsm2");

        Api.meshfxMsg("animLoop", 0, 0, "Take 001");
        Api.showHint('♪ Turn sound on ♪');

        Api.playVideo("foreground",true,1);

        Api.playSound("music.ogg", true, 1);
        Api.playVideo("frx",true,1);
        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        Api.stopSound("music.ogg");
        self.init();

    };

    this.faceActions = [scale(), this.hideHint];
    this.noFaceActions = [scale()];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

function scale() {
    var scaleFactor = 1;
    var currScale = 1.000000001;

    return function() {

        if (currScale > 1.03 || currScale <= 1) {
            scaleFactor = -scaleFactor;
        }
        
        currScale += 0.005 * scaleFactor;

        Api.meshfxMsg("shaderVec4", 0, 0, currScale + "0.0 0.0 0.0");
    };
}

configure(new Effect());