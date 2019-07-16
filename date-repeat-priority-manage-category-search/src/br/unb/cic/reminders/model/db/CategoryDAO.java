package br.unb.cic.reminders.model.db;

import java.util.List;

import br.unb.cic.framework.persistence.DBException;
import br.unb.cic.reminders.model.Category;

public interface CategoryDAO {

	public List<Category> listCategories() throws DBException;

	public Category findCategory(String name) throws DBException;

	public Category findCategoryById(Long id) throws DBException;

	public void saveCategory(Category category) throws DBException;

	public void updateCategory(Category category) throws DBException;

	public void deleteCategory(Category category) throws DBException;
}
