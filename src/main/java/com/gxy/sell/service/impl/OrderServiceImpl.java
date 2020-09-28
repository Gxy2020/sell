package com.gxy.sell.service.impl;

import com.gxy.sell.dataobject.OrderDetail;
import com.gxy.sell.dataobject.OrderMaster;
import com.gxy.sell.dataobject.ProductInfo;
import com.gxy.sell.dto.CartDTO;
import com.gxy.sell.dto.OrderDTO;
import com.gxy.sell.enums.ResultEnum;
import com.gxy.sell.exception.SellException;
import com.gxy.sell.repository.OrderDetailRepository;
import com.gxy.sell.repository.OrderMasterRepository;
import com.gxy.sell.service.OrderService;
import com.gxy.sell.service.ProductService;
import com.gxy.sell.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GUO
 * @Classname OrderServiceImpl
 * @Description 订单业务层实现类
 * @Date 2020/9/27 23:28
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Override
    @Transactional //添加事务
    public OrderDTO create(OrderDTO orderDTO) {
        String orderId=KeyUtil.getUniqueKey();
        BigDecimal orderAmount=new BigDecimal(BigInteger.ZERO);

       // List<CartDTO>catDTOList=new ArrayList<>();

        //1.查询商品(数量,价格)
        for (OrderDetail orderDetail:orderDTO.getOrderDetailList()){
            ProductInfo productInfo = productService.findOne(orderDetail.getProductId());
            if (productInfo==null){
                throw  new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            //2.计算订单总价
           //orderAmount= orderDetail.getProductPrice()*orderDetail.getProductQuantity(); //错误形式
//            orderAmount= orderDetail.getProductPrice()
            orderAmount= productInfo.getProductPrice() //重点
                    .multiply(new BigDecimal(orderDetail.getProductQuantity()))//先计算某一件商品的总价
                    .add(orderAmount);
            //订单详情入库
            orderDetail.setDetailId(KeyUtil.getUniqueKey());
            orderDetail.setOrderId(orderId);
//            orderDetail.setProductName(productInfo.getProductName());
            BeanUtils.copyProperties(productInfo,orderDetail);//对象属性的拷贝
            orderDetailRepository.save(orderDetail); //

//            CartDTO cartDTO=new CartDTO(orderDetail.getProductId(),orderDetail.getProductQuantity());
//            catDTOList.add(cartDTO);
        }
        //3.写入订单数据库(orderMaster和orderDetail)
        OrderMaster orderMaster=new OrderMaster();
        BeanUtils.copyProperties(orderDTO,orderMaster);//属性的值为null也会被拷贝
        orderMaster.setOrderId(orderId);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setCreateTime(new Date());
        orderMaster.setUpdateTime(new Date());
        orderMasterRepository.save(orderMaster);
        //4.扣掉库存
        List<CartDTO>catDTOList= orderDTO.getOrderDetailList().stream()
                .map(e-> new CartDTO(e.getProductId(),e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.decreaseStock(catDTOList);

        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {
        return null;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        return null;
    }

    @Override
    public OrderDTO cancel(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public OrderDTO finish(OrderDTO orderDTO) {
        return null;
    }

    @Override
    public OrderDTO paid(OrderDTO orderDTO) {
        return null;
    }
}