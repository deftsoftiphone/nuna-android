function Effect() {
    var self = this;

    this.isAnimPlay = false;

    this.data = [
        {
            file: "butt_00.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_01.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_02.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_03.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_04.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_05.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_06.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_07.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
        {
            file: "butt_08.bsm2",
            anims: [
                {a: "start", t: 4466},
                {a: "loop", t: 3333},
                {a: "finish",t: 3466},
            ]
        },
    ];

    this.butterflies = [];


    this.init = function () {
        Api.meshfxMsg("spawn", 11, 0, "tri.bsm2");
        Api.meshfxMsg("spawn", 10, 0, "!glfx_FACE");

        for (var i = 0; i < this.data.length - 1; i++) {
            var mesh = this.data[i];
            var butterfly = new Butterfly(mesh.file, i)
            .createAnimation(mesh.anims).init();
            this.butterflies.push(butterfly);
        }

        this.playDownAnims();

        Api.showHint("Shake your head");
        Api.playSound("music.ogg", true, 1);        

        this.faceActions.push(self.checkForHeadShake);

        Api.meshfxMsg("spawn", 9, 0, "morph.bsm2");
        Api.playVideo('foreground', true, 1);
        Api.showRecordButton();



    };

    this.playDownAnims = function () {
        var self = this;
        
        for (var i = 0; i < this.butterflies.length; i++) {
            var butterfly = this.butterflies[i];
            butterfly.playStartAnimation(this, function(anim) {
                anim.playLoop();
            });
        }
    };

    this.playUpAnims = function () {
        var self = this;
        
        for (var i = 0; i < this.butterflies.length; i++) {
            var butterfly = this.butterflies[i];
            var timeoutDelay = rand(0, 500);
            
            self.faceActions.push(
                timeout(timeOutCallBack, butterfly, timeoutDelay));
        }

        function timeOutCallBack(butterfly) {
            butterfly.playStopAnimation(self, function() {
                butterfly.playStartAnimation(self, function(anim) {
                    anim.playLoop();
                });
            });
        }

        function timeout(callback, callbackArgument, timeout) {
            var timer = new Date().getTime();
            var delay = timeout;
            
            return callCallbackAfterTimeout;

            function callCallbackAfterTimeout() {
                var now = new Date().getTime();
                if (now >= timer + delay) {
                    callback(callbackArgument);
                    var idx = self.faceActions.indexOf(callCallbackAfterTimeout);
                    self.faceActions.splice(idx, 1);
                }
            }
        }
    };

    this.checkForHeadShake = function() {
        var headPos = getHeadRotation();
        
        if (headPos != 0) {
            var index = self.faceActions.indexOf(self.checkForHeadShake);
            self.faceActions.splice(index, 1);
            self.faceActions.push(checkForHeadRotation(headPos));
        }

        function checkForHeadRotation(currCord) {
            var timer = new Date().getTime();
            var delay = 300;
            var facePosition = currCord;
            var counter = 1;
            return pushToArr;
            
            function pushToArr() {
                var now = new Date().getTime();
                
                if (now >= timer + delay) {
                    if (counter >= 2) {
                        self.trigger();
                    } else {
                        self.faceActions.push(self.checkForHeadShake);
                    }
                    var index = self.faceActions.indexOf(pushToArr);
                    self.faceActions.splice(index, 1);
                } else {
                    var currFacePosition = getHeadRotation();
                    if (currFacePosition === -facePosition) {
                        counter++;
                        facePosition = -facePosition;
                    }
                }
            }
        }
    };


    this.trigger = function() {
        Api.hideHint();
        this.playUpAnims();
        this.faceActions.push(self.checkForHeadShake);
    };

    this.restart = function () {
        Api.meshfxReset();
        self.init();
        Api.hideHint();
        //Api.stopSound("music.ogg");
    };

    this.faceActions = [];
    this.noFaceActions = [];

    this.videoRecordStartActions = [this.restart];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

function Animation(meshId, animationSet) {
    this.meshId = meshId;
    this.startAnim = animationSet[0];
    this.loopAnim = animationSet[1];
    this.stopAnim = animationSet[2];
    this.isPlay = false;
}

Animation.prototype.playStart = function (effect, callback) {
    Api.meshfxMsg("animOnce", this.meshId, 0, this.startAnim.a);
    var timer = new Date().getTime();
    this.isPlay = true;
    var self = this;
    if (effect) {
        effect.faceActions.push(callCallbackAfterTimeout);
    }

    function callCallbackAfterTimeout() {
        var now = new Date().getTime();

        if (now >= timer + self.startAnim.t) {
            var index = effect.faceActions.indexOf(callCallbackAfterTimeout);
            effect.faceActions.splice(index, 1);
            self.isPlay = false;
            if (callback) {
                callback(self);
            }
        }
    }
};

Animation.prototype.playLoop = function () {
    Api.meshfxMsg("animLoop", this.meshId, 0, this.loopAnim.a);
};

Animation.prototype.playStop = function (effect, callback) {
    var self = this;
    this.isPlay = true;

    Api.meshfxMsg("animOnce", this.meshId, 0, this.stopAnim.a);
    var timer = new Date().getTime();

    if (effect) {
        effect.faceActions.push(callCallbackAfterTimeout);
    }

    function callCallbackAfterTimeout() {
        var now = new Date().getTime();

        if (now >= timer + self.stopAnim.t + 2000) {
            var index = effect.faceActions.indexOf(callCallbackAfterTimeout);
            effect.faceActions.splice(index, 1);
            self.isPlay = false;
            if (callback) {
                callback(self);
            }
        }
    }
};

function Butterfly(meshName, id) {
    this.meshName = meshName;
    this.id = id;

    return this;
}

Butterfly.prototype.init = function () {
    Api.meshfxMsg("spawn", this.id, 0, this.meshName);

    return this;
};

Butterfly.prototype.createAnimation = function (animSet) {
    this.animation = new Animation(this.id, animSet);

    return this;
};

Butterfly.prototype.playStartAnimation = function (effect, callback) {
    if (!this.animation.isPlay) {
        this.animation.playStart(effect, callback);
    }

    return this;
};

Butterfly.prototype.PlayLoopAnimation = function () {
    this.animation.playLoop();

    return this;
};

Butterfly.prototype.playStopAnimation = function (effect, callback) {
    if (!this.animation.isPlay) {
        this.animation.playStop(effect, callback);
    }

    return this;
};

function mouthOpenAmount(landmarks, latents) {
	var latentsOffset = 0;
	var indicator = 0.0;
	if (latents[latentsOffset + 0] > 0 && latents[latentsOffset + 2] > 0) {
		indicator = Math.min(
            0.14 * latents[latentsOffset + 0] * latents[latentsOffset + 2], 1);
	}
	return indicator;
}

function getHeadRotation() {
	var rot;
	if (Api.getPlatform() == "macOS") {
        rot = mouthOpenAmount(world.landmarks, world.latents) - 0.5;
        if (rot >= -0.4) {
            return -1;
        } else if (rot <= -0.5) {
            return 0;
        } else {
            return 1;
        }
	} else {
		var mv = Api.modelview();
		rot = (-mv[2]) * 1.4;
	}

	if (rot <= -0.22222) return -1;
	if (rot <= 0.22222) return 0;
	else return 1;
}

function rand(min, max) {
    return min + Math.floor(Math.random() * (max - min));
}

configure(new Effect());