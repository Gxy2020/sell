package com.gxy.sell.service.impl;

import com.gxy.sell.dataobject.ProductCategory;
import com.gxy.sell.repository.ProductCategoryRepository;
import com.gxy.sell.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author GUO
 * @Classname CategoryServiceImpl
 * @Description TODO
 * @Date 2020/9/15 13:49
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
   private ProductCategoryRepository repository;
    @Override
    public ProductCategory findById(Integer categoryId) {
        return repository.findById(categoryId).get();
    }

    /**
     * 查询分类列表
     * @return
     */
    @Override
    public List<ProductCategory> findAll() {
        return repository.findAll();
    }

    /**
     * 按产品分类id查询分类的信息
     * @param categoryTypeList
     * @return
     */
    @Override
    public List<ProductCategory> findByCategoryTypeIn(List<Integer>categoryTypeList) {
        return repository.findByCategoryTypeIn(categoryTypeList);
    }

    /**
     * 添加商品分类
     * @param productCategory
     * @return
     */
    @Override
    public ProductCategory save(ProductCategory productCategory) {
        return repository.save(productCategory);
    }
}
