function Effect() {
    var self = this;

    this.init = function() {
        Api.meshfxMsg("spawn", 2, 0, "!glfx_FACE");
        Api.meshfxMsg("spawn", 0, 0, "BeautyFaceSP_Optimased.bsm2");
        Api.meshfxMsg("spawn", 4, 0, "fire.bsm2");
        Api.meshfxMsg("spawn", 3, 0, "tri2.bsm2");
        Api.playSound("music.ogg", true, 1);

        Api.playVideo("frx",false,1);

        timeOut(9000, function() {
            Api.playVideoRange("frx", 3,  9, true, 1);
        });

        timeOut(3000, function() { Api.hideHint(); });
        Api.meshfxMsg( "shaderVec4", 0, 1, 0.7 + " 0.0 0.0 0.0");
        Api.showHint("Open mouth");
        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        Api.stopVideo("frx");
        Api.stopSound("music.ogg");
        self.init();
        Api.hideHint();
    };

    this.faceActions = [onUpdate, alphaPulse()];
    this.noFaceActions = [];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

var isSpawned = false;

var effect = new Effect();

function onUpdate() {
    if (isMouthOpen(world.landmarks, world.latents)) {
        if (!isSpawned) {
            Api.hideHint();
            Api.playSound("sfx.ogg", true, 1);
            Api.meshfxMsg("spawn", 1, 0, "eyes_fire.bsm2");
            isSpawned = true;
            changeBGAlpha(true);
        }
    } else {
        if (isSpawned) {
            Api.stopSound("sfx.ogg");
            Api.meshfxMsg("del", 1);
            isSpawned = false;
            changeBGAlpha(false);
        }
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

function alphaPulse() {
    var isPositive = false;
    var startAlpha = 1.0;

    var lastTime = new Date().getTime();
    var deltaList = [16, 16, 16, 16, 16];

    return function() {
        var now =  new Date().getTime();
        var delta = now - lastTime;
        lastTime = now;

        deltaList.pop();
        deltaList.unshift(delta);
        
        var averageDelata = average(deltaList);

        if (!isPositive) {
            startAlpha -= averageDelata * 0.001;

            if (startAlpha <= 0.4) {
                startAlpha = 0.4;
                isPositive = true;
            }
        } else {
            startAlpha += averageDelata * 0.001;

            if (startAlpha >= 1.0) {
                startAlpha = 1.0;
                isPositive = false;
            }
        }

        Api.meshfxMsg( "shaderVec4", 0, 0, startAlpha + "0.0 0.0 0.0");
    };
}

function average(arr) {
    return arr.reduce(function(prev, curr) {return prev + curr;}) / arr.length;
}

function changeBGAlpha(isOut) {
    var isPositive = isOut;
    var startAlpha = isOut? 0.7 : 1.0;

    var lastTime = new Date().getTime();
    var deltaList = [16, 16, 16, 16, 16];

    effect.faceActions.push(removeAfterTrigger);

    function removeAfterTrigger() {
        var now =  new Date().getTime();
        var delta = now - lastTime;
        lastTime = now;

        deltaList.pop();
        deltaList.unshift(delta);
        
        var averageDelata = average(deltaList);

        if (!isPositive) {
            startAlpha -= averageDelata * 0.01;

            if (startAlpha <= 0.7) {
                startAlpha = 0.7;
                trigger();
            }
        } else {
            startAlpha += averageDelata * 0.01;

            if (startAlpha >= 1.0) {
                startAlpha = 1.0;
                trigger();
            }
        }

        Api.meshfxMsg( "shaderVec4", 0, 1, startAlpha + "0.0 0.0 0.0");
    }

    function trigger() {
        var idx = effect.faceActions.indexOf(removeAfterTrigger);
        effect.faceActions.splice(idx, 1);
    }
}

configure(effect);