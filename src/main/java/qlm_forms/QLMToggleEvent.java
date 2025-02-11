package qlm_forms;

import necesse.engine.events.GameEvent;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormComponent;

public class QLMToggleEvent extends GameEvent {
	public final FormManager formManager;
	public final FormComponent QLMButtonForm;
	public final boolean buttonState;

	public QLMToggleEvent(FormManager formManager, FormComponent qlm_form, boolean button_state) {
		this.formManager = formManager;
		this.QLMButtonForm = qlm_form;
		this.buttonState = button_state;
	}
}