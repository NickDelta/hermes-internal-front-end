package org.hua.hermes.frontend.component;

import com.vaadin.flow.component.textfield.TextField;

public class TrimmedTextField extends TextField
{
    public TrimmedTextField()
    {
    }

    public TrimmedTextField(String label)
    {
        super(label);
    }

    public TrimmedTextField(String label, String placeholder)
    {
        super(label, placeholder);
    }

    public TrimmedTextField(String label, String initialValue, String placeholder)
    {
        super(label, initialValue, placeholder);
    }

    public TrimmedTextField(ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener)
    {
        super(listener);
    }

    public TrimmedTextField(String label, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener)
    {
        super(label, listener);
    }

    public TrimmedTextField(String label, String initialValue, ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener)
    {
        super(label, initialValue, listener);
    }

    @Override
    public String getValue()
    {
        return super.getValue().trim();
    }
}
