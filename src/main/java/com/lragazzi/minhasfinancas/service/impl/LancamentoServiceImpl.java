package com.lragazzi.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import com.lragazzi.minhasfinancas.exception.RegraNegocioException;
import com.lragazzi.minhasfinancas.model.entity.Lancamento;
import com.lragazzi.minhasfinancas.model.enums.StatusLancamento;
import com.lragazzi.minhasfinancas.model.repository.LancamentoRepository;
import com.lragazzi.minhasfinancas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {

	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
		
	}

	@Override
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {

		Example example = Example.of(lancamentoFiltro, 
				ExampleMatcher.matching()
						.withIgnoreCase()
						.withStringMatcher(StringMatcher.CONTAINING));
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		this.atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		//Validar campo descrição  
		if(lancamento.getDescricao() == null || lancamento.getDescricao().equals("") ){
			throw new RegraNegocioException("Informe uma descrição válida.");
		}

		//Validar campo mês  
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12 ){
			throw new RegraNegocioException("Informe um mês válido.");
		}		

		//Validar campo ano  
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ){
			throw new RegraNegocioException("Informe um ano válido.");
		}		

		//Validar usuário  
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
			throw new RegraNegocioException("Informe um usuário.");
		}		
		
		//Validar campo valor
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
			throw new RegraNegocioException("Informe um valor válido.");
		}

		//Validar campo tipo
		if(lancamento.getTipo() == null){
			throw new RegraNegocioException("Informe um tipo de lançamento.");
		}
		
		
		
		
	}

}
