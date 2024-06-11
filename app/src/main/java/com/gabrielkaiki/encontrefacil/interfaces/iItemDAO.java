package com.gabrielkaiki.encontrefacil.interfaces;

import com.gabrielkaiki.encontrefacil.models.Item;

import java.util.List;

public interface iItemDAO {
    boolean salvar(Item item);

    boolean deletar(Item item);

    List<Item> listar();
}
