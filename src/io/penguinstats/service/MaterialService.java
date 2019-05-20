package io.penguinstats.service;

import java.util.List;

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

}
