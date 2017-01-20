/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.necs.maximus.ui.beans;

import com.necs.maximus.db.entity.Has;
import com.necs.maximus.db.entity.IsSubstitute;
import com.necs.maximus.db.entity.Product;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Luis Castañeda <luis.castaneda at acorde.com.ve>
 */
@Named(value = "dialogProductInfoController")
@ViewScoped
public class DialogProductInfoController extends AbstractController<Product> {

    private List<Has> quoteListByProduct;
    private List<IsSubstitute> partListSubstitutes;
    private String partNumber;

    public DialogProductInfoController() {
        super(Product.class);
    }

    public List<Has> getQuoteListByProduct() {

        return quoteListByProduct;
    }

    public void setQuoteListByProduct(List<Has> quoteListByProduct) {
        this.quoteListByProduct = quoteListByProduct;
    }

    public List<IsSubstitute> getPartListSubstitutes() {

        return partListSubstitutes;
    }

    public void setPartListSubstitutes(List<IsSubstitute> partListSubstitutes) {
        this.partListSubstitutes = partListSubstitutes;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

}
