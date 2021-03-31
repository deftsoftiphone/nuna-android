
function Effect() {
	var self = this;


	this.hintSolver = function() {
		now = (new Date()).getTime();

        if (now > self.removeHint) {
            Api.hideHint();
            self.faceActions = [];
        }
	};

	this.init = function() {
		Api.meshfxMsg("spawn", 1, 0, "!glfx_FACE");
		Api.meshfxMsg("spawn", 0, 0, "Rabbit.bsm2");

		Api.playSound("Rabbit_sndfnt.ogg", true, 1);
		Api.playVideo("backgroundSeparation", true, 1);
		Api.playVideo("frx",true,1);
		Api.showRecordButton();

        if (Api.getPlatform().toLowerCase() == "ios") {
            Api.showHint("Voice changer ");
		};

		self.removeHint = (new Date()).getTime() + 5000;

		self.faceActions = [self.hintSolver];
	};

	this.restart = function() {
		Api.meshfxReset();
		// Api.stopVideo("frx");
		// Api.stopSound("sfx.aac");
		self.init();
	};

	this.startSound = function () {
		Api.stopSound("Rabbit_sndfnt.ogg");
		Api.playSound("Rabbit_sndfnt.ogg", true, 1);
	};

	this.stopSound = function () {
		if(Api.getPlatform() == "ios") {
			Api.hideHint();
			Api.stopSound("Rabbit_sndfnt.ogg");			
		};
	}	

	this.faceActions = [];
	this.noFaceActions = [];

	this.videoRecordStartActions = [self.stopSound];
	this.videoRecordFinishActions = [];
	this.videoRecordDiscardActions = [self.startSound];
}

configure(new Effect());
