package QuestLogMinimizer;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.gfx.forms.MainGameFormManager;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = MainGameFormManager.class, name="updateActive", arguments = {boolean.class})
public class QLMinimizerPatch{

	@Advice.OnMethodExit
	public static void loadedGameInit(@Advice.This MainGameFormManager f){
		if (QuestLogMinimizer.getQLMDebugState()) QuestLogMinimizer.oops("updateActive patched event triggered.");
		QuestLogMinimizer.GetCurrentInstance().mainFormUpdateEvent(f);
	}
}