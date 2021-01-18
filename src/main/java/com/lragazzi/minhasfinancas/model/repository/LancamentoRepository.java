package com.lragazzi.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lragazzi.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
