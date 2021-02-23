package com.lragazzi.minhasfinancas.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lragazzi.minhasfinancas.api.dto.LancamentoDTO;
import com.lragazzi.minhasfinancas.exception.RegraNegocioException;
import com.lragazzi.minhasfinancas.model.entity.Lancamento;
import com.lragazzi.minhasfinancas.model.entity.Usuario;
import com.lragazzi.minhasfinancas.model.enums.StatusLancamento;
import com.lragazzi.minhasfinancas.model.enums.TipoLancamento;
import com.lragazzi.minhasfinancas.service.LancamentoService;
import com.lragazzi.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor //cria o construtor da classe que inicializa lancamentoService e usuarioService
public class LancamentoResource {
	
	private final LancamentoService lancamentoService;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(
		@RequestParam(value = "descricao", required = false) String descricao,
		@RequestParam(value = "mes", required = false) Integer mes,
		@RequestParam(value = "ano", required = false) Integer ano,
		@RequestParam("usuario") Long idUsuario
		){
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if(usuario.isPresent()){
			lancamentoFiltro.setUsuario(usuario.get());
		
		}else{
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o id informado.");
		}
		
		List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO lancamentoDTO){
		
		try{
			Lancamento lancamento = converter(lancamentoDTO);
			lancamento = lancamentoService.salvar(lancamento);
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
			
		}catch (RegraNegocioException e){
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO){
		
		return lancamentoService.obterPorId(id).map( entity -> {

			try{
				Lancamento lancamento = converter(lancamentoDTO);
				lancamento.setId(lancamento.getId());
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			
			}catch(RegraNegocioException e){
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> 
			new ResponseEntity("Lançamento não encontrado na base Dados.", HttpStatus.BAD_REQUEST) );
		
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO){
		
		return lancamentoService.obterPorId(id).map( entity -> {

			try{
				StatusLancamento statusLancamento = StatusLancamento.valueOf(lancamentoDTO.getStatus());
				
				if(statusLancamento == null){
					return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
				}
				entity.setStatus(statusLancamento);
				lancamentoService.atualizar(entity);
				return ResponseEntity.ok(entity);
			
			}catch(RegraNegocioException e){
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> 
			new ResponseEntity("Lançamento não encontrado na base Dados.", HttpStatus.BAD_REQUEST) );
		
	}	
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id){
		return lancamentoService.obterPorId(id).map( lancamento -> {
			lancamentoService.deletar(lancamento);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			
		}).orElseGet(() -> 
			new ResponseEntity("Lançamento não encontrado na base Dados.", HttpStatus.BAD_REQUEST) );

	}
	
	private Lancamento converter(LancamentoDTO lancamentoDTO){
		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao(lancamentoDTO.getDescricao());
		lancamento.setAno(lancamentoDTO.getAno());
		lancamento.setMes(lancamentoDTO.getMes());
		lancamento.setValor(lancamentoDTO.getValor());
		
		Usuario usuario = usuarioService.obterPorId(lancamentoDTO.getUsuario())
		.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado"));
		
		lancamento.setUsuario(usuario);
		lancamento.setTipo(TipoLancamento.valueOf(lancamentoDTO.getTipo()));
		lancamento.setStatus(StatusLancamento.valueOf(lancamentoDTO.getStatus()));
		
		return lancamento;
	}
	
	
}
