package com.lragazzi.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lragazzi.minhasfinancas.api.dto.UsuarioDTO;
import com.lragazzi.minhasfinancas.exception.ErroAutenticacao;
import com.lragazzi.minhasfinancas.exception.RegraNegocioException;
import com.lragazzi.minhasfinancas.model.entity.Usuario;
import com.lragazzi.minhasfinancas.model.repository.LancamentoRepository;
import com.lragazzi.minhasfinancas.service.LancamentoService;
import com.lragazzi.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor //cria o construtor da classe que inicializa o usuarioService
public class UsuarioResource {
	
//	@GetMapping("/")
//	public String helloWord(){
//		return "hello word!";
//	}

	private final UsuarioService usuarioService;
	private final LancamentoService lancamentoService;
	
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
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id){

	   Optional<Usuario> usuario = usuarioService.obterPorId(id);
	   
	   if(!usuario.isPresent()){
		   return new ResponseEntity(HttpStatus.NOT_FOUND);
	   }

	   BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		
	   return ResponseEntity.ok(saldo);

	}
	
	
}
