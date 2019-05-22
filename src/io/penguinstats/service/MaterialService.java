package io.penguinstats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.penguinstats.bean.Material;
import io.penguinstats.dao.MaterialDao;

public class MaterialService {

	private static MaterialService instance = new MaterialService();
	private static MaterialDao dao = new MaterialDao();

	private MaterialService() {}

	public static MaterialService getInstance() {
		return instance;
	}

	public boolean saveMaterial(Material material) {
		return dao.save(material);
	}

	public Material getMaterial(int id) {
		return dao.findByID(id);
	}

	public List<Material> getAllMaterials() {
		return dao.findAll();
	}

	public Map<Integer, Material> getMaterialMap() {
		List<Material> list = getAllMaterials();
		Map<Integer, Material> map = new HashMap<>();
		list.forEach(material -> map.put(material.getId(), material));
		return map;
	}

}
