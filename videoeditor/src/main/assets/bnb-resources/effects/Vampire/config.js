
function Effect() {
	var self = this;

	// this.meshes = [
	// 	{ file:"vampire.bsm2", anims:[
	// 		{ a:"start", t:2300 },
	// 	] },
	// ];

	this.play = function() {
		var now = (new Date()).getTime();
		if(now > self.t) {
			Api.meshfxMsg("spawn", 0, 0, "vampire_physics.bsm2");
			/*Bones:
B_chain01
B_earrings_right01
B_earrings_left01
B_ring
B_chain02       -> B_chain01
B_chain03       -> B_chain02
B_earrings_right02      -> B_earrings_right01
B_earrings_left02       -> B_earrings_left01
B_chain04       -> B_chain03
B_earrings_right03      -> B_earrings_right02
B_earrings_left03       -> B_earrings_left02
B_chain05       -> B_chain04
*/
			Api.meshfxMsg("dynImass", 0, 0, "B_chain01");
			Api.meshfxMsg("dynImass", 0, 0, "B_earrings_right01");
			Api.meshfxMsg("dynImass", 0, 0, "B_earrings_left01");
			Api.meshfxMsg("dynImass", 0, 0, "B_ring");

			Api.meshfxMsg("dynImass", 0, 1000, "B_earrings_right03");
			Api.meshfxMsg("dynImass", 0, 1000, "B_earrings_left03");
			Api.meshfxMsg("dynImass", 0, 1000, "B_chain05");

			Api.meshfxMsg("dynConstraint", 0, 99, "B_chain02 ~B_chain01");

			//Api.meshfxMsg("dynDamping", 0, 97, "");
			Api.meshfxMsg("dynGravity", 0, 0, "0 -1000 0");

			self.faceActions = [];

		}
	};

	this.init = function() {
		Api.meshfxMsg("spawn", 1, 0, "vampire_bat.bsm2");
		Api.meshfxMsg("animOnce", 1, 0, "start");
		Api.meshfxMsg("spawn", 0, 0, "vampire_accessories.bsm2");
		Api.meshfxMsg("animOnce", 0, 0, "start");

		self.t = (new Date()).getTime() + 2300;
		self.faceActions = [self.play];
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
