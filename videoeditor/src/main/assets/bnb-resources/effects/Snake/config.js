function Effect() {
    var self = this;
    
    this.waitTime = 0;
    
    this.startAnimDuration  = 8000;
    this.idleAnimDuration   = 2233.3333;
    this.attackAnimDuration = 1333.3333;

    this.snakeAnim = function() {
        var now = (new Date()).getTime();
        if( now > self.waitTime ) {

                var max = 5500
                var min = 100
                var randomNum = Math.floor(Math.random()*(max-min+1)+min)

                Api.meshfxMsg( "animOnce", 0, 0, "attack" );
                Api.stopSound("Snake_bite_SFX.ogg");
                Api.playSound("Snake_bite_SFX.ogg", false, 1);
                self.waitTime = now + self.attackAnimDuration + randomNum;
                
            //}
        }
    }
    
    this.waitForTrigger = function() {
        if( isMouthOpen( world.landmarks, world.latents ) ) {
            //Api.recordStart(15);
            Api.hideHint();
        
            Api.meshfxMsg( "spawn",    3, 0, "hide_mouth.bsm2" );
            Api.meshfxMsg( "spawn",    1, 0, "Head.bsm2" );
            Api.meshfxMsg( "spawn",    0, 0, "SnakeAnimation03.bsm2" );
            Api.meshfxMsg( "animOnce", 0, 0, "Start" );
            
            Api.playSound("Snake_idle_SFX.ogg", false, 1);
            
            self.waitTime = (new Date()).getTime() + self.startAnimDuration;
            self.startTime = (new Date()).getTime();
            self.faceActions = [self.snakeAnim];
            //self.faceActions();

            
        }
    };
    
    this.init = function() {
        Api.meshfxMsg( "spawn",    2, 0, "!glfx_FACE" );
        Api.showRecordButton();
        Api.showHint( "Open mouth" );
        Api.playSound("Snake_BGM.ogg", true, 1);
    };
    
    this.onVideoDiscardAction = function() {
        Api.showHint( "Open mouth" );
        Api.meshfxReset();
        Api.stopSound("Snake_bite_SFX.ogg");
        Api.stopSound("Snake_idle_SFX.ogg");
        self.faceActions = [self.waitForTrigger];
        self.init();
    };
    

    this.faceActions = [this.waitForTrigger];
    //this.faceActions = [this.waitForTrigger];
    this.noFaceActions = [];
    
    this.videoRecordStartActions = [this.onVideoDiscardAction];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.onVideoDiscardAction];
    
};

configure( new Effect() );
