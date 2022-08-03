package com.example.netane;

import java.util.List;

public class CustomListItem {
    private List<Integer> Numbers;
    private List<String> HitBlow;

    /**
     * 空のコンストラクタ
     */
    public CustomListItem() {};

    /**
     * コンストラクタ
     * @param numbers 入力値
     * @param hit_blow 結果
     */
    public CustomListItem(List<Integer> numbers, List<String> hit_blow) {
        Numbers = numbers;
        HitBlow = hit_blow;
    }

    /**
     * 入力を設定
     * @param numbers 入力
     */
    public void setNumbers(List<Integer> numbers) {
        Numbers = numbers;
    }

    /**
     * タイトルを設定
     * @param hit_blow 結果
     */
    public void setHitBlow(List<String> hit_blow) {
        HitBlow = hit_blow;
    }

    /**
     * 入力値を取得
     * @return 入力値
     */
    public List<Integer> getNumbers() {
        return Numbers;
    }

    /**
     * 結果を取得
     * @return 結果
     */
    public List<String> getHitBlow() {
        return HitBlow;
    }
}
