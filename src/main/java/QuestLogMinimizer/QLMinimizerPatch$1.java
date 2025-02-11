package QuestLogMinimizer;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.gfx.forms.MainGameFormManager;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = MainGameFormManager.class, name="setup", arguments = {})
public class QLMinimizerPatch$1{

	@Advice.OnMethodExit
	public static void loadedGameInit(@Advice.This MainGameFormManager f){
		if (QuestLogMinimizer.getQLMDebugState()) QuestLogMinimizer.oops("Form setup patched event triggered. Will it blend?");
		QuestLogMinimizer.GetCurrentInstance().gameLoadedEvent(f);
	}
}