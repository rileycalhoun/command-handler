package me.rileycalhoun.commandhandler.common;

import me.rileycalhoun.commandhandler.common.exception.InvalidHelpPageException;
import org.jetbrains.annotations.Range;

import java.util.List;

public interface CommandHelp<T> extends List<T> {

    CommandHelp<T> paginate(int page, int elementsPerPage) throws InvalidHelpPageException;

    @Range(from = 1, to = Long.MAX_VALUE)
    int getPageSize(int elementsPerPage);

}
