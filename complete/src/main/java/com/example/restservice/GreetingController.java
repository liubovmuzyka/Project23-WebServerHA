package com.example.restservice;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GreetingController {

	boolean light = false;
	boolean stateMusik = false;
	boolean stateEmail = false;
	double latitude, longtitude;
	String updateStateMusik;
	String updateStateEmail;
	float batteryState = 0;
	int countEmail = 0;
	double distance = 0;

	@GetMapping("/state")
	public String greeting() {
		return "State: " + light;
	}

	@GetMapping("/paket")
	public String greetingpakettest() {
		System.out.println("PAKET ANGEKOMMEN");
		return "PAKET ZURUECK ";
	}

	@RequestMapping(value = "/switch2", method = RequestMethod.GET)
	@ResponseBody
	public boolean switch2Get(HttpServletRequest request) throws Exception {
		//System.out.println("switch2Get");
		return light ; //? " {state:on}" : " {state:off}";
	}

	@RequestMapping(value = "/getStatusMusic", method = RequestMethod.GET)
	@ResponseBody
	public String getStatusMusic(HttpServletRequest request) throws Exception {
		//System.out.println("switch2Get");
		return stateMusik ? " {state Music:on}" : " {state Music:off}";
	}

	@RequestMapping(value = "/switch2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean switch2Post(HttpServletRequest request) throws Exception {
		switch2Put(request);
		return light;
	}

	@RequestMapping(value = "/getStateEmail", method = RequestMethod.GET)
	@ResponseBody
	public boolean getStateEmail(HttpServletRequest request) throws Exception {
		return stateEmail;
	}

	@RequestMapping(value = "/switch2", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean switch2Patch(HttpServletRequest request) throws Exception {
		return switch2Put(request);
	}

	@RequestMapping(value = "/switch2", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public boolean switch2Put(HttpServletRequest request) throws Exception {
		//System.out.println("switch2Put");

		JSONObject data_obj = new JSONObject(IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8));
		System.out.println(data_obj.toString());
		boolean newState;
		Object stateInRequest = data_obj.get("state");
		newState = ((String) stateInRequest).equalsIgnoreCase("on");
		System.out.println("Old state: " + light + ", New state: " + newState);
		light = newState;
		return switch2Get(request);
	}



	@RequestMapping(value = "/updateGPS", method = RequestMethod.GET)
	@ResponseBody
	public boolean updateGPS(HttpServletRequest request) throws Exception {

		String content = request.getParameterMap().entrySet()
				.stream()
				.map(e -> e.getKey() + "=\"" + Arrays.toString(e.getValue()) + "\"")
				.collect(Collectors.joining(", "));
		//System.out.println(content);

		latitude = Double.valueOf(request.getParameterMap().get("latitude")[0]);
		longtitude = Double.valueOf(request.getParameterMap().get("longtitude")[0]);

		//System.out.println(latitude);
		//System.out.println(longtitude);

		double latitude2 = 49.747510;
		double longtitude2= 6.675483;
		//System.out.println("Distance between server and android: " + distFrom(latitude, longtitude, latitude2, longtitude2) + " m");

		if (distFrom(latitude, longtitude, latitude2, longtitude2) != 0.2){
			setDistance(distFrom(latitude, longtitude, latitude2, longtitude2)/1000);
			System.out.println("Distance is " + distFrom(latitude, longtitude, latitude2, longtitude2)+" m, swith off the light");
			light = false;
		}
		return switch2Get(request);
	}

	@RequestMapping(value = "/updateStatusMusic", method = RequestMethod.GET)
	@ResponseBody
	public String updateStatusMusic(HttpServletRequest request) throws Exception {

		String content = request.getParameterMap().entrySet()
				.stream()
				.map(e -> e.getKey() + "=\"" + Arrays.toString(e.getValue()) + "\"")
				.collect(Collectors.joining(", "));
		//System.out.println(content);

		updateStateMusik = request.getParameterMap().get("stateMusic")[0];

		if (updateStateMusik.toString().equals("on")){
			System.out.println("Chaged Status Music: " + updateStateMusik);
			stateMusik=true;
		}else {
			System.out.println("Chaged Status Music: " + updateStateMusik);
			stateMusik=false;
		}
		return getStatusMusic(request);
	}


	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; //meters
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
						Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);

		return dist;
	}


	@RequestMapping(value = "/switch", method = RequestMethod.POST)
	@ResponseBody
	public String greetingJson(HttpServletRequest request) throws Exception {
		final String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		System.out.println("json = " + payload);

		/*String content = request.getParameterMap().entrySet()
				.stream()
				.map(e -> e.getKey() + "=\"" + Arrays.toString(e.getValue()) + "\"")
				.collect(Collectors.joining(", "));
		System.out.println(content);*/

		JSONObject data_obj = new JSONObject(payload);
		boolean newState;
		Object stateInRequest = data_obj.get("state");
		if(stateInRequest instanceof Boolean) {
			newState = (Boolean) stateInRequest;
		} else if(stateInRequest instanceof String) {
			if(((String) stateInRequest).equalsIgnoreCase("on")) {
				newState = true;
			} else if(((String) stateInRequest).equalsIgnoreCase("off")) {
				newState = false;
			} else {
				throw new IllegalArgumentException("state must be on or off");
			}
			newState = ((String) stateInRequest).equalsIgnoreCase("on");
		} else {
			throw new IllegalArgumentException("state must be on or off");
		}
		System.out.println("Old state: " + light + ", New state: " + newState);
		light = newState;

		return "State: " + light;
	}

	@RequestMapping(value = "/updateStateEmail", method = RequestMethod.GET)
	@ResponseBody
	public boolean updateStateEmail(HttpServletRequest request) throws Exception {

		String content = request.getParameterMap().entrySet()
				.stream()
				.map(e -> e.getKey() + "=\"" + Arrays.toString(e.getValue()) + "\"")
				.collect(Collectors.joining(", "));
		//System.out.println(content);

		updateStateEmail = request.getParameterMap().get("stateEmail")[0];

		if (updateStateEmail.toString().equals("on")){
			System.out.println("Chaged State Email: " + updateStateEmail);
			stateEmail=true;
		}else {
			System.out.println("Chaged State Email: " + updateStateEmail);
			stateEmail=false;
		}
		return getStateEmail(request);
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}


	@RequestMapping(value = "/getBatteryState", method = RequestMethod.GET)
	@ResponseBody
	public float getBatteryState(HttpServletRequest request) throws Exception {
		return batteryState;
	}

	@RequestMapping(value = "/updateBatteryState", method = RequestMethod.GET)
	@ResponseBody
	public float updateBatteryState(HttpServletRequest request) throws Exception {

		String content = request.getParameterMap().entrySet()
				.stream()
				.map(e -> e.getKey() + "=\"" + Arrays.toString(e.getValue()) + "\"")
				.collect(Collectors.joining(", "));
		//System.out.println(content);

		batteryState = Float.valueOf(request.getParameterMap().get("batteryState")[0]);

		return getBatteryState(request);
	}

	@RequestMapping(value = "/updateEmailCount", method = RequestMethod.GET)
	@ResponseBody
	public int updateEmailCount(HttpServletRequest request) throws Exception {

		String content = request.getParameterMap().entrySet()
				.stream()
				.map(e -> e.getKey() + "=\"" + Arrays.toString(e.getValue()) + "\"")
				.collect(Collectors.joining(", "));
		//System.out.println(content);

		countEmail = Integer.valueOf(request.getParameterMap().get("countEmail")[0]);

		return getEmailCount(request);
	}

	@RequestMapping(value = "/getDistance", method = RequestMethod.GET)
	@ResponseBody
	public double getDistance(HttpServletRequest request) throws Exception {
		return getDistance();
	}

	@RequestMapping(value = "/getEmailCount", method = RequestMethod.GET)
	@ResponseBody
	public int getEmailCount(HttpServletRequest request) throws Exception {
		return countEmail;
	}
}
