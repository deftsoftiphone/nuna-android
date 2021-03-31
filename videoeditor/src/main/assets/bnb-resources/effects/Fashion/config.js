function Effect() {
    var self = this;

    this.init = function() {
        Api.meshfxMsg("spawn", 2, 0, "!glfx_FACE");

        Api.meshfxMsg("spawn", 0, 0, "3d2dfashion.bsm2");
        Api.meshfxMsg("animLoop", 0, 0, "Take 001");
        
        Api.meshfxMsg("tex", 0, 0, glass_base_colors[3]);

        Api.meshfxMsg("spawn", 1, 0, "BeautyFaceSP.bsm2");
        Api.meshfxMsg("animLoop", 1, 0, "Take 001");
        Api.playVideo("frx", true, 1.0);
        Api.playSound("music.ogg", true, 1);
        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        Api.stopSound("music.ogg");
        self.init();
    };

    this.faceActions = [checkIfStarted];
    this.noFaceActions = [checkIfStarted];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

var glass_base_colors = [
    "1_glass_BaseColor.png", "2_glass_BaseColor.png",
    "3_glass_BaseColor.png", "4_glass_BaseColor.png"
];

var isPlaying = false;

function checkIfStarted() {
    if (!isPlaying){
        effect.noFaceActions.push(glassAnimation);
        // effect.FaceActions.push(glassAnimation);
    }
}

function glassAnimation(){
    isPlaying = true;
    timeOut(500, function() { 
        Api.meshfxMsg("tex", 0, 0, glass_base_colors[0]);
    });
    timeOut(1000, function() { 
        Api.meshfxMsg("tex", 0, 0, glass_base_colors[1]);
    });
    timeOut(1500, function() { 
        Api.meshfxMsg("tex", 0, 0, glass_base_colors[2]);
    });
    timeOut(2000, function() { 
        Api.meshfxMsg("tex", 0, 0, glass_base_colors[3]);
    });

    isPlaying = false;
    var idx = effect.noFaceActions.indexOf(glassAnimation);
            effect.noFaceActions.splice(idx, 1);
            idx = effect.faceActions.indexOf(glassAnimation);
            effect.faceActions.splice(idx, 1);
}

function timeOut(delay, callback) {
    var timer = new Date().getTime();

    effect.faceActions.push(removeAfterTimeOut);
    effect.noFaceActions.push(removeAfterTimeOut);

    function removeAfterTimeOut() {
        var now = new Date().getTime();
        
        if (now >= timer + delay) {
            var idx = effect.faceActions.indexOf(removeAfterTimeOut);
            effect.faceActions.splice(idx, 1);
            idx = effect.noFaceActions.indexOf(removeAfterTimeOut);
            effect.noFaceActions.splice(idx, 1);
            callback();
        }
    }
}
configure(new Effect());