
function Effect() {
	var self = this;

	this.init = function() {
		Api.meshfxMsg("spawn", 1, 0, "!glfx_FACE");

		Api.meshfxMsg("spawn", 0, 0, "Thanksgiving_circlet.bsm2");
		Api.meshfxMsg("animLoop", 0, 0, "Take 001");

		// self.faceActions = [self.play];
		// Api.showHint("Open mouth");
		// Api.playVideo("frx",true,1);
		// Api.playSound("sfx.aac",false,1);
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

	this.videoRecordStartActions = [this.restart];
	this.videoRecordFinishActions = [];
	this.videoRecordDiscardActions = [this.restart];
}

configure(new Effect());
