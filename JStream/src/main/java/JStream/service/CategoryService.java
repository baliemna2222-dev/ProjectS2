package JStream.service;

import java.util.List;

import JStream.dao.CategoryDAO;
import JStream.entity.Category;

public class CategoryService {

    private CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getAllCategories(){
        return categoryDAO.getAllCategories();
    }

    public void addCategory(Category category){
        categoryDAO.addCategory(category);
    }

    public void updateCategory(Category category){
        categoryDAO.updateCategory(category);
    }

    public void deleteCategory(int id){
        categoryDAO.deleteCategory(id);
    }
}