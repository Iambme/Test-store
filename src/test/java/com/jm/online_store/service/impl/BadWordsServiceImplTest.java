package com.jm.online_store.service.impl;

import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BadWordsServiceImplTest {
    private BadWordsRepository badWordsRepository = mock(BadWordsRepository.class);
    private CommonSettingsService commonSettingsService = mock(CommonSettingsService.class);
    private BadWordsServiceImpl badWordsService = new BadWordsServiceImpl(badWordsRepository, commonSettingsService);
    private CommonSettings templateBody = new CommonSettings(1L, "bad_words_enabled", "true", false);


    private List<BadWords> allActiveBW;
    private List<String> expectedBadWords;
    private String incoming;
    private String incoming2;


    @Test
    void importWord() {
        incoming2 = "Winter, , g, Spring, Summer, Autumn, h";
        badWordsService.importWord(incoming2);
        verify(badWordsRepository, times(4)).existsBadWordsByBadword(any(String.class));
        verify(badWordsRepository, times(4)).save(any(BadWords.class));
    }

    @Test
    void preparingWordsForImport() {
        incoming="Winter,   ,    g, Spring,,      Sum - mer,   Autumn,    765,";
        String[] expected= {"Winter", "", "g", "Spring,", "Sum - mer", "Autumn", "765"};
        try {
            Method method = BadWordsServiceImpl.class.getDeclaredMethod("preparingWordsForImport", String.class);
            method.setAccessible(true);
            assertArrayEquals(expected, (Object[]) method.invoke(badWordsService, incoming));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    void CheckComment() {
        allActiveBW = Arrays.asList(
                new BadWords("????????????????", true),
                new BadWords("??????????????", false),
                new BadWords("????????????????", true),
                new BadWords("??????", true));
        incoming = "lol'???? ???? ????????????????!!!.??????????????' - ???????????????? ??????:'?????? ???? ?????????? ?????????????? ?????????????????? ????????????????...GGH5'";
        expectedBadWords = Arrays.asList("????????????????", "??????????????", "????????????????");
        Mockito.when(badWordsService.findAllWordsActive()).thenReturn(allActiveBW);
        Mockito.when(commonSettingsService.getSettingByName("bad_words_enabled")).thenReturn(templateBody);
        List<String> actual = badWordsService.checkComment(incoming);
        assertEquals(expectedBadWords, actual);
    }
}
