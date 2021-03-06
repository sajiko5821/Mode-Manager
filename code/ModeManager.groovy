/**
 *  Mode Manager
 *
 *  Copyright 2021 Lukas Weier
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Mode Manager",
    namespace: "sajiko5821",
    author: "Lukas Weier",
    description: "A SmartApp to manage multiple Modes for your home",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/sajiko5821/Mode-Manager/main/Icons/Presence_Manager_Icon_V1.png",
    iconX2Url: "https://raw.githubusercontent.com/sajiko5821/Mode-Manager/main/Icons/Presence_Manager_Icon_V1.png",
    iconX3Url: "https://raw.githubusercontent.com/sajiko5821/Mode-Manager/main/Icons/Presence_Manager_Icon_V1.png")

preferences {
	page(name: "mainPage", title: "Select matching Time and Modes", install: true, uninstall: true, submitOnChange: true){
    	section("Morning"){
        	input "morningMode", "mode", title: "Select Morning Mode", required: true
            input "morningFrom", "time", title: "From",required: true
        }
        
        section("Day"){
        	input "dayMode", "mode", title: "Select Day Mode", required: true
            input "dayFrom", "time", title: "From", required: true
      	}
        
        section("Evening"){
        	input "eveningMode", "mode", title: "Select Evening Mode", required: true
            input "eveningFrom", "time", title: "From", required: false
      	}
        
        section("Night"){
        	input "nightMode", "mode", title: "Select Night Mode", required: true
            input "nightFrom", "time", title: "From", required: true
     	}       
        
        section("Midnight"){
        	input "midnightMode", "mode", title: "Select Midnight Mode", required: true
            input "midnightFrom", "time", title: "From", required: true
        }
        
        section("Away"){
        	input "awayMode", "mode", title: "Select Away Mode", required: true
            input "awaySensor", "capability.contactSensor", title: "Select Door Sensor", required: true
        }
        
        section("Send Push Notification?"){
        	input "sendPush", "bool", title: "Send Push Notification when Mode is changed", required:false
        }
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(awaySensor, "contact.open", doorOpenHandler)
    subscribe(awaySensor, "contact.closed", doorClosedHandler)
    schedule(morningFrom, morningHandler)
    schedule(dayFrom, dayHandler)
    schedule(eveningFrom, eveningHandler)
    schedule(nightFrom, nightHandler)
    schedule(midnightFrom, midnightHandler)
}

def doorClosedHandler(evt){
	setLocationMode("Away")
    log.debug "Location Mode Away"
    if(sendPush){
    	sendPush("Mode changed to Away")
    }
}

def doorOpenHandler(evt){
	log.debug "doorOpenHandler successfull"
	def morningTime = timeOfDayIsBetween(morningFrom, dayFrom, new Date(), location.timeZone)
    def dayTime = timeOfDayIsBetween(dayFrom, eveningFrom, new Date(), location.timeZone)
    def eveningHandler = timeOfDayIsBetween(eveningFrom, nightFrom, new Date(), location.timeZone)
    def nightTime = timeOfDayIsBetween(nightFrom, midnightFrom, new Date(), location.timeZone)
    def midnightTime = timeOfDayIsBetween(midnightFrom, morningFrom, new Date(), location.timeZone)
    
        if(morningTime){
            setLocationMode("Morning")
    		log.debug "Location Mode Morning"
    		if(sendPush){
    			sendPush("Mode changed to Morning")
    		}
        }

        if(dayTime){
            setLocationMode("Day")
        	log.debug "Location Mode Day"
        	if(sendPush){
            	sendPush("Mode changed to Day")
        	}
        }
        
        if(eveningTime){
        	setLocationMode("Evening")
       		log.debug "Location Mode Evening"
       		if(sendPush){
        		sendPush("Mode changed to Evening")
      		}
        }

        if(nightTime){
        	setLocationMode("Night")
        	log.debug "Location Mode Night"
        	if(sendPush){
            	sendPush("Mode changed to Night")
        	}
        }

        if(midnightTime){
            setLocationMode("Midnight")
        	log.debug "Location Mode Midnight"
        	if(sendPush){
            	sendPush("Mode changed to Midnight")
        	}
        }

        else{
            nightHandler()
        }
}

def morningHandler(evt){
	if(location.mode != "Away"){
		setLocationMode("Morning")
    	log.debug "Location Mode Morning"
    	if(sendPush){
    		sendPush("Mode changed to Morning")
    	}
 	}
}

def dayHandler(evt){
	if(location.mode != "Away"){
        setLocationMode("Day")
        log.debug "Location Mode Day"
        if(sendPush){
            sendPush("Mode changed to Day")
        }
  	}
}

def eveningHandler(evt){
   	if(location.mode != "Away"){
      	setLocationMode("Evening")
       	log.debug "Location Mode Evening"
       	if(sendPush){
        	sendPush("Mode changed to Evening")
      	}
  	}
}

def nightHandler(evt){
   	if(location.mode != "Away"){
        setLocationMode("Night")
        log.debug "Location Mode Night"
        if(sendPush){
            sendPush("Mode changed to Night")
        }
 	}
}

def midnightHandler(evt){
        if(location.mode != "Away"){
        setLocationMode("Midnight")
        log.debug "Location Mode Midnight"
        if(sendPush){
            sendPush("Mode changed to Midnight")
        }
  	}
}