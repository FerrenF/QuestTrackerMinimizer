package QuestLogMinimizer;

import java.util.Collection;

import necesse.engine.GameLog;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.engine.state.State;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormComponentListTyped;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormQuestTrackedComponent;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import qlm_forms.QuestTrackerMinimizeButtonForm;
	
@ModEntry
public class QuestLogMinimizer {

	//public static int QUEST_TRACKER_MINIMIZED_HEIGHT = 40; @deprecated - interestingly enough, the first idea for this was to max/min the form by squishing it.
	public static int COMPONENT_INITIAL_PUSH_AMOUNT_X = 30;
	public static int COMPONENT_EVENT_PUSH_AMOUNT_X = -500;
	private static final boolean debug_output = false; // setting this to true will produce a LOT of text.
	
	private MainGameFormManager mainGameFormManager; // unused/planned. i'm sure i'll have some use eventually.
	private TrackedSidebarForm questTrackerComponent;
	private QuestTrackerMinimizeButtonForm currentMinimizerForm;
	private static QuestLogMinimizer CurrentInstance;
	private boolean game_initialized = false;
	private int stored_position_x = 0; // used for emergency position reset
	private boolean stored_state = true; //  used to persist maximization between levels in the future.

	public static void oops(String how) {
		GameLog.out.println(how);
	}
		
    public void init() {
    	GameLog.out.println("QuestLogMinimizer initialized.");
        if(CurrentInstance == null) CurrentInstance = this;         
    }
    
    public void setStoredState(boolean state) {
    	this.stored_state = state;
    }
    
    public void gameLoadedEvent(MainGameFormManager formManager) {    	
    	
    	if (getQLMDebugState()) oops("LoadedEvent triggered.");
    	
    	// Set class references to current FormManager and necessary components.
    	this.mainGameFormManager = formManager;        	    	
    	TrackedSidebarForm qf = findGameFormComponent(formManager.getComponents(), TrackedSidebarForm.class);
    	if(qf == null) {
    		if (getQLMDebugState()) oops("Couldn't find the TrackedSidebarForm responsible for quests. Well shid. ");
    		return;
    	}
    	this.questTrackerComponent = qf;
    	
    	QuestTrackerMinimizeButtonForm nf = new QuestTrackerMinimizeButtonForm() {
			@Override
			public void onToggleButtonState(QuestTrackerMinimizeButtonForm var1, boolean var2) {
				QuestLogMinimizer.GetCurrentInstance().qlmToggleEventTriggered(var1, var2);				
			}
    		
    	};   
    	this.currentMinimizerForm = nf;

    	doInitialComponentBump();
    	qf.addComponent(nf);  
    	this.game_initialized = true;
    }
    
    public void mainFormUpdateEvent(MainGameFormManager formManager) {    	
    	
    	// Periodically, the game will redraw the components on parts of the MainGameFormManager without initializing a new form.
    	// We want to intercept this and then check for the presence of our button.
    	
    	// First, let's ensure the game is initialized.
    	if (!this.game_initialized) return;
    	if (this.questTrackerComponent == null)return;
    	if (getQLMDebugState()) oops("Form Update Event Called");
    	   	
    	// Set class references to current FormManager.
    	this.mainGameFormManager = formManager;         	 	
    	    	
    	// If our minimizer button is not on the quest tracker, then we should re-add it.
    	if(!this.questTrackerComponent.hasComponent(this.currentMinimizerForm)) {
    		QuestTrackerMinimizeButtonForm nf = new QuestTrackerMinimizeButtonForm() {
    			@Override
    			public void onToggleButtonState(QuestTrackerMinimizeButtonForm var1, boolean var2) {
    				QuestLogMinimizer.GetCurrentInstance().qlmToggleEventTriggered(var1, var2);				
    			}
        		
        	};   
        	// And then save it to a field.
        	this.currentMinimizerForm = nf;
        	
        	// It's likely we will need to do our initial 'bump' too.
    		this.doInitialComponentBump();
    		this.questTrackerComponent.addComponent(nf);   		
    		
    		// Finally, restore the stored state. We only need to do this is the stored state is false (minimized). This should be triggered through the form.
    		if(!this.stored_state) this.currentMinimizerForm.setButtonState(this.stored_state);
    	}
    }
    
    public void qlmToggleEventTriggered(QuestTrackerMinimizeButtonForm source, boolean state){
    	if (getQLMDebugState()) oops("qlmToggleEventTriggered method called with button state "+String.valueOf(state));
    	this.setStoredState(state);
    	this.doEventBump(state);    		
    }
    public void doInitialComponentBump() {
    	if (this.questTrackerComponent == null)return;    	
    	for (FormComponent f : this.questTrackerComponent.getComponents()) {
    	    if (f instanceof FormPositionContainer) {
    	        FormPositionContainer positionContainer = (FormPositionContainer) f;
    	        int newX = positionContainer.getX() + COMPONENT_INITIAL_PUSH_AMOUNT_X;
    	        positionContainer.setX(newX);
    	        
    	        if (getQLMDebugState()) QuestLogMinimizer.oops("Bumped " + f.getClass().getSimpleName() + " to X: " + newX);
    	    }
    	}
    }
    public void doEventBump(boolean state) {
    	for (FormComponent f : this.questTrackerComponent.getComponents()) {
    	    if (f instanceof FormQuestTrackedComponent) {
    	    	FormQuestTrackedComponent positionContainer = (FormQuestTrackedComponent) f;
    	    	
    	    	// Depending on the button state, we are either going to bump the components that make up the individual quest descriptions off the screen
    	    	// Or we will return them to their normal position.
    	    	positionContainer.setX(positionContainer.getX() + (COMPONENT_EVENT_PUSH_AMOUNT_X * (state ? -1 : 1)));
    	        if (getQLMDebugState()) oops("Bumped " + f.getClass().getSimpleName() + " to X: " + positionContainer.getX());
    	    }
    	}
    }
    
    public static boolean getQLMDebugState() {
    	return debug_output;
    }
    
    public static QuestLogMinimizer GetCurrentInstance() {
    	return CurrentInstance;
    }
    
    public static MainGame getMainGameState() {    	
        State currentState = necesse.engine.GlobalData.getCurrentState();
        return currentState instanceof MainGame ? (MainGame) currentState : null;
    }   
    
    public static Client getGameClient() {    	
        State currentState = necesse.engine.GlobalData.getCurrentState();
        return currentState instanceof MainGame ? ((MainGame) currentState).getClient() : ((MainMenu) currentState).getClient();
    }    
    
    // This method recurses through the collection of FormComponents, and if those components also have components - recurses through those too.
    // It's great for discovering the structure of how forms are arranged in the MainGameFormManager
    // It's also resource intensive, so optimally we only use it once on initialization.
    public static <T extends FormComponent> T findGameFormComponent(Collection<FormComponent> currentComponents, Class<T> targetClass) {    
        if (currentComponents == null) {
        	if (getQLMDebugState()) oops("Attempt to retrieve main form components resulted in null.");
            return null;
        }    
        
        for (FormComponent f : currentComponents) {
            
        	if (getQLMDebugState()) oops("findGameFormComponent found: " + f.getClass().descriptorString() );          

            T result = findGameFormComponentRecursive(f, targetClass);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }

    private static <T extends FormComponent> T findGameFormComponentRecursive(FormComponent component, Class<T> targetClass) {
        if (targetClass.isInstance(component)) {
        	if (getQLMDebugState()) oops("Found target component: " + component.getClass().getSimpleName());
            return targetClass.cast(component);
        }
   
        if (getQLMDebugState()) oops("Searching inside: " + component.getClass().descriptorString() );
        
        if (component instanceof FormComponentListTyped) {
        	if (getQLMDebugState()) oops("Recursing into FormComponentList");
            for (FormComponent subComponent : ((FormComponentList) component).getComponentList()) {            	        	 
                T result = findGameFormComponentRecursive(subComponent, targetClass);
                if (result != null) {
                    return result;
                }
            }
        }

        if (component instanceof FormContentBox) {
        	if (getQLMDebugState()) oops("Recursing into FormContentBox");
            for (FormComponent subComponent : ((FormContentBox) component).getComponentList()) {
                T result = findGameFormComponentRecursive(subComponent, targetClass);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}
