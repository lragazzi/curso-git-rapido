package com.lragazzi.minhasfinancas.api.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UsuarioResource {
	
	@GetMapping("/")
	public String helloWord(){
		return "hello word!";
	}

	
	
}