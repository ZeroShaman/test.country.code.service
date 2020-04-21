package ru.test.countrycodeservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.test.countrycodeservice.domain.CountryEntity;

public interface CountryEntityRepository extends JpaRepository<CountryEntity, Long> {

	@Query("select e from CountryEntity e where lower(e.title) like ?1%")
	public List<CountryEntity> searchByTitleStartsWidth(String title);

	public List<CountryEntity> findAll();
}