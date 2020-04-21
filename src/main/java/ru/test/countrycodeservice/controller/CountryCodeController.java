package ru.test.countrycodeservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import ru.test.countrycodeservice.domain.CountryEntity;
import ru.test.countrycodeservice.repository.CountryEntityRepository;

@RestController
public class CountryCodeController {

	@Autowired
	private Environment env;

	@Autowired
	private CountryEntityRepository repository;

	Logger log = LoggerFactory.getLogger(CountryCodeController.class);

	/**
	 * Метод получения телефонных кодов
	 * 
	 * @param filterTitle
	 *            - фильтр поиска по наименованию страны
	 * 
	 * @return - список данных по странам, удовлетворяющим условиям поиска
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/rest/code")
	@ResponseBody
	public List<CountryEntity> countryList(@RequestParam(name = "country", required = false) String filterTitle) {
		List<CountryEntity> countryList = new ArrayList<CountryEntity>();
		filterTitle = filterTitle != null ? filterTitle.toLowerCase() : "";

		final RestTemplate restTemplate = new RestTemplate();
		String nameUrl = env.getProperty("service.country.names.url");
		String phoneUrl = env.getProperty("service.country.phone.url");
		Map<String, String> countryNameMap = null;
		Map<String, String> countryPhoneMap = null;
		// получаем данные из сервиса
		try {
			countryNameMap = restTemplate.getForObject(nameUrl, HashMap.class);
			countryPhoneMap = restTemplate.getForObject(phoneUrl, HashMap.class);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		// Если данные из сервиса не доступны - считываем данные из БД
		if (countryNameMap == null || countryNameMap.isEmpty() || countryPhoneMap == null
				|| countryPhoneMap.isEmpty()) {
			countryList = repository.searchByTitleStartsWidth(filterTitle);
			return countryList;
		}

		// Если данные получены из сервиса - заполняем список странами,
		// удовлетворяющими поисковому запросу
		countryList = getCountryEntityList(countryNameMap, countryPhoneMap, filterTitle);
		return countryList;
	}

	/**
	 * Метод получения телефонных кодов
	 * 
	 * @param filterName
	 *            - фильтр поиска по наименованию страны
	 * 
	 * @return - список данных по странам, удовлетворяющим условиям поиска
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/rest/update")
	@ResponseBody
	public ResponseEntity<Boolean> countryUpdate() {
		List<CountryEntity> countryList = new ArrayList<CountryEntity>();

		final RestTemplate restTemplate = new RestTemplate();
		String nameUrl = env.getProperty("service.country.names.url");
		String phoneUrl = env.getProperty("service.country.phone.url");
		Map<String, String> countryNameMap = null;
		Map<String, String> countryPhoneMap = null;

		try {
			// получаем данные из сервиса
			countryNameMap = restTemplate.getForObject(nameUrl, HashMap.class);
			countryPhoneMap = restTemplate.getForObject(phoneUrl, HashMap.class);

			// Если данные из сервиса не доступны - выход
			if (countryNameMap == null || countryNameMap.isEmpty() || countryPhoneMap == null
					|| countryPhoneMap.isEmpty()) {
				throw new Exception("Внешний сервер не доступен.");
			}

			// Если данные получены из сервиса - заполняем БД, удаляя старые
			// записи
			countryList = getCountryEntityList(countryNameMap, countryPhoneMap, null);
			repository.deleteAll();
			repository.saveAll(countryList);
		} catch (Exception e) {
			log.error("Не удалось получить данные из внешнего сервера. Подробнее: " + e.getMessage());
			return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	/**
	 * Возвращает список стран
	 * 
	 * @param countryNameMap
	 * @param countryPhoneMap
	 * @param filterTitle
	 * @return
	 */
	private List<CountryEntity> getCountryEntityList(Map<String, String> countryNameMap,
			Map<String, String> countryPhoneMap, String filterTitle) {
		List<CountryEntity> countryList = new ArrayList<CountryEntity>();

		if (countryNameMap == null || countryNameMap.isEmpty() || countryPhoneMap == null
				|| countryPhoneMap.isEmpty()) {
			return countryList;
		}

		for (Entry<String, String> entity : countryNameMap.entrySet()) {
			if (filterTitle == null || entity.getValue().toLowerCase().startsWith(filterTitle)) {
				CountryEntity country = new CountryEntity();
				country.setShortTitle(entity.getKey());
				country.setTitle(entity.getValue());
				country.setCode(countryPhoneMap.get(country.getShortTitle()));
				countryList.add(country);
			}
		}
		return countryList;
	}
}
