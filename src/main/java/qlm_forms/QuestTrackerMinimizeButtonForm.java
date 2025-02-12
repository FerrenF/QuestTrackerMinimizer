package qlm_forms;

import QuestLogMinimizer.QuestLogMinimizer;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.ui.ButtonStateTextures;
import necesse.gfx.ui.GameInterfaceStyle;

public abstract class QuestTrackerMinimizeButtonForm extends Form {
	
	// Use the default game interface style to draw forms that have inherent textures. 
	// Default style: "primal" located in gfx/ui/primal/*
	private static GameInterfaceStyle DefaultGameInterfaceStyle = GameInterfaceStyle.getStyle(GameInterfaceStyle.defaultPath);
	private static ButtonStateTextures bst_Maximize = new ButtonStateTextures(DefaultGameInterfaceStyle,"button_maximize");
	private static ButtonStateTextures bst_Minimize = new ButtonStateTextures(DefaultGameInterfaceStyle,"button_minimize");
	
	private boolean button_state_maximized = true;
	private FormIconButton QTMFormButtonComponent = null;	
	private boolean formDebugState = false;
	public QuestTrackerMinimizeButtonForm() {
		super("QuestTrackerMinimizeButtonForm", 24, 24);
	
		// This form should not be draggable. When the single argument of setDraggingBox is null, the base class disables dragging.
		this.setDraggingBox(null);		
		this.init();
	}
	public QuestTrackerMinimizeButtonForm(boolean debug_state) {	
		this();
		this.formDebugState = debug_state;		
	}
	public void init() {
		
		//Helper: FormIconButton (int x, int y, ButtonStateTextures textures, int width, int height, GameMessage... tooltips)
		this.setPosition(5, 5);
		FormComponent iconComponent = new FormIconButton(2, 2, this.getButtonStateTextures(), 20, 20, new StaticMessage(this.getButtonStateTooltipString()));
		this.QTMFormButtonComponent = ((FormIconButton) this.addComponent(iconComponent));
		this.registerEvents();
	}
	public void registerEvents() {
		this.QTMFormButtonComponent.onClicked((e) -> {
			this.toggleButtonState();
		});
	}
	
	// Fired when the value of button_state_maximized changes
	public abstract void onToggleButtonState(QuestTrackerMinimizeButtonForm var1, boolean var2);
	
	public String getButtonStateTooltipString() {
		// If the button state is minimized, then display 'Maximize' or some variance as the tooltip text. Otherwise, display 'Minimize'/etc;
		return necesse.engine.localization.Localization.translate("core", this.button_state_maximized ? "qtminimize" : "qtmaximize");
	}
	
	public boolean setButtonState(boolean state) {
		
		// SET the button state flag, and then the tooltip and textures based on the resulting state.
	    this.button_state_maximized = state;
	    this.QTMFormButtonComponent.setTooltips(new StaticMessage(this.getButtonStateTooltipString()));
	    this.QTMFormButtonComponent.textures = getButtonStateTextures();
	    
	    // Trigger toggle event
	    this.onToggleButtonState(this, this.button_state_maximized);
	    
	    // Debug information.
	    if (this.formDebugState) QuestLogMinimizer.oops("Quest tracker minimizer button state set to " + (this.button_state_maximized ? "on" : "off"));		
	   
	    return this.button_state_maximized;
	}
	public boolean toggleButtonState() {		
		// Toggle button state flag, and then the tooltip and textures based on the resulting state.
	    this.setButtonState(!this.button_state_maximized);	  	   
	    return this.button_state_maximized;
	}
	
	public ButtonStateTextures getButtonStateTextures() {
		// Change the texture used for the icon in this form based on the current state flag.
		return button_state_maximized ? bst_Minimize : bst_Maximize;
	}    
}