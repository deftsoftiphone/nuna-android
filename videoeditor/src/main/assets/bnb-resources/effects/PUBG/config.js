function Effect() {
    var self = this;
	var hintDisplayed = false;
	var isHelmetOn = false;
	var animReadyTime = 0;
    
    this.waitTrigger = function() {
		now  = (new Date()).getTime();
		if (now > animReadyTime)
		{
			if (isSmile(world.landmarks, world.latents))
			{
				if (isHelmetOn)
				{
					Api.hideHint();
					Api.meshfxMsg("animOnce", 4, 0, "trigger_close");					
					animReadyTime = now + 1566;	
					
				}
				else
				{
					Api.hideHint();
					Api.meshfxMsg("animOnce", 4, 0, "trigger_open");				
					animReadyTime = now + 1433;
					
				}
				isHelmetOn = !isHelmetOn;
			}
		}
	}

    this.init = function() {
        Api.meshfxMsg("spawn", 5, 0, "!glfx_FACE");

        Api.meshfxMsg("spawn", 0, 0, "cut_01.bsm2");
        // Api.meshfxMsg("animOnce", 0, 0, "static");

        Api.meshfxMsg("spawn", 1, 0, "face.bsm2");
        // Api.meshfxMsg("animOnce", 1, 0, "static");

        
        // Api.meshfxMsg("animOnce", 2, 0, "static");

        Api.meshfxMsg("spawn", 3, 0, "aka_01.bsm2");
        // Api.meshfxMsg("animOnce", 3, 0, "static");

        Api.meshfxMsg("spawn", 4, 0, "helmet.bsm2");
        // Api.meshfxMsg("animOnce", 4, 0, "static");
        // Api.meshfxMsg("animOnce", 4, 1, "open");
        // Api.meshfxMsg("animOnce", 4, 2, "close");

        if(Api.getPlatform() == "ios" || Api.getPlatform() == "macOS"){
            Api.meshfxMsg("shaderVec4", 0, 0, String("1.0"));
        }
        else {
            Api.meshfxMsg("spawn", 2, 0, "cut_blur.bsm2");
            Api.meshfxMsg("shaderVec4", 0, 0, String("0.0"));
        }

        

        if(hintDisplayed != true)
		{
			Api.showHint("Smile");
			hintDisplayed = true;
        };
        
        Api.playSound("music_pubg_L_Ch.ogg",true,1);
        Api.showRecordButton();
    };

    this.restart = function() {
        Api.meshfxReset();
        // Api.stopVideo("frx");
        Api.stopSound("music_pubg_L_Ch.ogg");
        self.init();
    };

    this.faceActions = [self.waitTrigger];
    this.noFaceActions = [];

    this.videoRecordStartActions = [];
    this.videoRecordFinishActions = [];
    this.videoRecordDiscardActions = [this.restart];
}

configure(new Effect());