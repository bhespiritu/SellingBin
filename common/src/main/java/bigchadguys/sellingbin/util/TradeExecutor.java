package bigchadguys.sellingbin.util;

import bigchadguys.sellingbin.init.ModBlocks;
import bigchadguys.sellingbin.trade.Trade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.*;

public class TradeExecutor {

    public static Result test(Trade trade, ScreenHandler handler) {
        Result result = new Result();
        result.setExpired(false);
        Map<Integer, ItemStack> slots = new LinkedHashMap<>();

        for(Slot slot : handler.slots) {
           slots.put(slot.id, slot.getStack().copy());
        }

        for(int i = 1; i == 1; i++) {
            Trade.Input input = trade.getInput();
            int left = input.getCount();

            List<Integer> affectedSlots = new ArrayList<>();

            for(Map.Entry<Integer, ItemStack> entry : slots.entrySet()) {
                ItemStack stack = entry.getValue();
                if(left <= 0) break;
                if(!input.getFilter().test(stack)) continue;
                int difference = Math.min(stack.getCount(), left);
                stack.decrement(difference);
                left -= difference;
                affectedSlots.add(entry.getKey());
            }

            if(left <= 0) {
                result.addUsedSlots(affectedSlots);
                result.setInput(i, true);
            } else {
                result.addIncompleteSlots(affectedSlots);
                result.setInput(i, false);
            }
        }

        ItemStack output = trade.getOutput().generate().orElse(new ItemStack(ModBlocks.ERROR.get()));
        int left = output.getCount();

        for(Map.Entry<Integer, ItemStack> entry : slots.entrySet()) {
            ItemStack stack = entry.getValue();
            if(left <= 0) break;

            if(stack.isEmpty() || ItemStack.canCombine(stack, output)) {
                int difference = Math.min(output.getMaxCount() - stack.getCount(), left);
                ItemStack copy = output.copy();
                copy.setCount(stack.getCount() + difference);
                left -= difference;
            }
        }

        result.setOutput(left <= 0);
        return result;
    }

    public static void execute(Trade trade, ScreenHandler handler) {
        for(int i = 1; i == 1; i++) {
            Trade.Input input = trade.getInput();
            int left = input.getCount();

            for(Slot slot : handler.slots) {
                ItemStack stack = slot.getStack();
                if(left <= 0) break;
                if(!input.getFilter().test(stack)) continue;
                int difference = Math.min(stack.getCount(), left);
                stack.decrement(difference);
                slot.setStack(stack);
                left -= difference;
            }
        }

        ItemStack output = trade.getOutput().generate().orElse(new ItemStack(ModBlocks.ERROR.get()));
        int left = output.getCount();

        for(Slot slot : handler.slots) {
            ItemStack stack = slot.getStack();
            if(left <= 0) break;

            if(stack.isEmpty() || ItemStack.canCombine(stack, output)) {
                int difference = Math.min(output.getMaxCount() - stack.getCount(), left);
                ItemStack copy = output.copy();
                copy.setCount(stack.getCount() + difference);
                slot.setStack(copy);
                left -= difference;
            }
        }
    }

    public static class Result {
        private boolean[] inputs;
        private boolean output;
        private boolean expired;
        private List<Integer> usedSlots;
        private List<Integer> incompleteSlots;

        public Result() {
            this.inputs = new boolean[3];
            this.usedSlots = new ArrayList<>();
            this.incompleteSlots = new ArrayList<>();
        }

        public boolean checkInput(int index) {
            return this.inputs[index - 1];
        }

        public void setInput(int index, boolean completion) {
            this.inputs[index - 1] = completion;
        }

        public boolean checkOutput() {
            return this.output;
        }

        public void setOutput(boolean completion) {
            this.output = completion;
        }

        public boolean isExpired() {
            return this.expired;
        }

        public void setExpired(boolean expired) {
            this.expired = expired;
        }

        public List<Integer> getUsedSlots() {
            return this.usedSlots;
        }

        public void addUsedSlots(Collection<Integer> slots) {
            this.usedSlots.addAll(slots);
        }

        public List<Integer> getIncompleteSlots() {
            return this.incompleteSlots;
        }

        public void addIncompleteSlots(Collection<Integer> slots) {
            this.incompleteSlots.addAll(slots);
        }

        public boolean canTrade() {
            return this.inputs[0] && this.inputs[1] && this.inputs[2] && this.output && !this.expired;
        }
    }

}
