package io.github.steaf23.bingoreloaded.item.tasks;

public interface CountableTask extends TaskData {
    int getCount();

    CountableTask updateTask(int newCount);
}
