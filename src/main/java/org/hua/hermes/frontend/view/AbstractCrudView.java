package org.hua.hermes.frontend.view;

import com.vaadin.componentfactory.enhancedcrud.Crud;
import com.vaadin.componentfactory.enhancedcrud.CrudEditor;
import com.vaadin.componentfactory.enhancedcrud.CrudEditorPosition;
import com.vaadin.componentfactory.enhancedcrud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;

import static org.hua.hermes.frontend.constant.MessageConstants.DELETE_MESSAGE;
import static org.hua.hermes.frontend.constant.MessageConstants.DISCARD_MESSAGE;

public abstract class AbstractCrudView<T> extends Crud<T>
{
    private final String entityName;

    public AbstractCrudView(Class<T> entityType, String entityName, CrudEditor<T> editor)
    {
        super(entityType, new Grid<>(), editor);
        this.entityName = entityName;

        this.getGrid().setSelectionMode(Grid.SelectionMode.SINGLE);

        setupDataProvider();
        setupGrid();
        this.setEditorPosition(CrudEditorPosition.ASIDE);

        //Functional requirements don't have a user delete option so far
        this.getDelete().setEnabled(false);
        this.getDelete().getStyle().set("visibility","hidden");

        setI18n(setupI18n());
        setupEventListeners();
        setSizeFull();
    }

    public abstract void setupGrid();
    public abstract void setupDataProvider();
    public abstract void setupEventListeners();

    public CrudI18n setupI18n()
    {
        CrudI18n crudI18n = CrudI18n.createDefault();
        crudI18n.setNewItem("New " + entityName);
        crudI18n.setEditItem("Edit " + entityName);
        crudI18n.setEditLabel("Edit");
        crudI18n.getConfirm().getCancel().setContent(DISCARD_MESSAGE);
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, entityName));
        crudI18n.setDeleteItem("Delete");
        return crudI18n;
    }
}
