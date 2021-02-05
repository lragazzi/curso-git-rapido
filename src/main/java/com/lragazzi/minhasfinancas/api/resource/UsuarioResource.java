package com.lragazzi.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lragazzi.minhasfinancas.api.dto.UsuarioDTO;
import com.lragazzi.minhasfinancas.exception.ErroAutenticacao;
import com.lragazzi.minhasfinancas.exception.RegraNegocioException;
import com.lragazzi.minhasfinancas.model.entity.Usuario;
import com.lragazzi.minhasfinancas.service.UsuarioService;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {
	
//	@GetMapping("/")
//	public String helloWord(){
//		return "hello word!";
//	}

	private UsuarioService usuarioService;
	
	public UsuarioResource(UsuarioService usuarioService){
		this.usuarioService = usuarioService;
	}

	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO usuarioDTO){
		
		try{
			Usuario usuario = usuarioService.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
			return ResponseEntity.ok(usuario);
			
		}catch(ErroAutenticacao e){
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO usuarioDTO){
		
		Usuario usuario = Usuario.builder()
				.nome(usuarioDTO.getNome())
				.email(usuarioDTO.getEmail())
				.senha(usuarioDTO.getSenha()).build();
		
		try {
			usuario = usuarioService.salvarUsuario(usuario);
			return new ResponseEntity(usuario, HttpStatus.CREATED);
			
		}catch (RegraNegocioException e){
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		

	}
	
	
	
}
