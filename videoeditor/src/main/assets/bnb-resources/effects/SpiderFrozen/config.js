var isAnimPlyay = false;

function Effect() {
    var self = this;

    this.init = function() {
        Api.meshfxMsg("spawn", 2, 0, "!glfx_FACE");
        Api.meshfxMsg("spawn", 0, 0, "FaceMorph.bsm2");
        Api.showRecordButton();
        Api.playVideo("foreground", true, 1);
        Api.playSound("music.ogg", true, 1);
        Api.showHint("Open mouth");
    };

    this.restart = function() {
        Api.meshfxReset();
        Api.stopSound("music.ogg");
        self.init();
    };

    this.faceActions = [onUpdate];
    this.noFaceActions = [];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

function onUpdate() {
    if(isMouthOpen(world.landmarks, world.latents)) {
        trigger();
    }
}

function trigger() {
    if (!isAnimPlyay) {
        Api.hideHint();
        Api.meshfxMsg("spawn", 1, 0, "NABlackWidow.bsm2");
        Api.meshfxMsg("animOnce", 1, 0, "Armature.001|Armature.001|Take 001|Take 001:BaseAnimation");
        isAnimPlyay = true;
        timeOut(7375, function() {
            isAnimPlyay = false;
            Api.meshfxMsg("del", 1);
        });
    }
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