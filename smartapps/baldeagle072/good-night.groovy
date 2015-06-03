/**
 *  Good Night
 *  
 *  Use a button to change to night mode
 *
 *  Author: baldeagle072@gmail.com
 *  Date: 2014-01-08
 */

// Automatically generated. Make future change here.
definition(
    name: "Good Night",
    namespace: "baldeagle072",
    author: "baldeagle072@gmail.com",
    description: "Uses a button to trigger",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png")

preferences {
	section("When this button is pressed") {
		input "button", "capability.switch", title: "Which?"
	}
	section("Change to this mode") {
		input "newMode", "mode", title: "Mode?"
	}
    section("Turn off these lights") {
    	input "switches", "capability.switch", title: "Which?", required: false, multiple: true
    }
    section("Choose thermostat... ") {
		input "thermostat", "capability.thermostat", required: false
	}
	section("Heat setting...") {
		input "heatingSetpoint", "number", title: "Degrees Fahrenheit?", required: false
	}
	section( "Notifications" ) {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
		input "phoneNumber", "phone", title: "Send a Text Message?", required: false
	}

}

def installed() {
	log.debug "Current mode = ${location.mode}"
	createSubscriptions()
}

def updated() {
	log.debug "Current mode = ${location.mode}"
	unsubscribe()
	createSubscriptions()
}

def createSubscriptions()
{
	subscribe(button, "switch.on", onHandler)
	subscribe(location, modeChangeHandler)
    subscribe(thermostat, "heatingSetpoint", heatingSetpointHandler)
	subscribe(thermostat, "temperature", temperatureHandler)

	if (state.modeStartTime == null) {
		state.modeStartTime = 0
	}
}

def modeChangeHandler(evt) {
	state.modeStartTime = now()
}


def onHandler(evt) {
	log.debug "changeMode, location.mode = $location.mode, newMode = $newMode, location.modes = $location.modes"
	switches?.off()
    thermostat.setHeatingSetpoint(heatingSetpoint)
    thermostat.poll()
    if (location.mode != newMode) {
		if (location.modes?.find{it.name == newMode}) {
			setLocationMode(newMode)
			send "${label} has changed the mode to '${newMode}'"
		}
		else {
			send "${label} tried to change to undefined mode '${newMode}'"
		}
	}
}

def heatingSetpointHandler(evt)
{
	log.debug "heatingSetpoint: $evt, $settings"
}

def coolingSetpointHandler(evt)
{
	log.debug "coolingSetpoint: $evt, $settings"
}

def temperatureHandler(evt)
{
	log.debug "currentTemperature: $evt, $settings"
}

private send(msg) {
	if ( sendPushMessage != "No" ) {
		log.debug( "sending push message" )
		sendPush( msg )
	}

	if ( phoneNumber ) {
		log.debug( "sending text message" )
		sendSms( phoneNumber, msg )
	}

	log.debug msg
}