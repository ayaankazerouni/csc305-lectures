package main;

import java.util.ArrayList;
import java.util.List;

public class BirdTest {
    public static void main(String[] args){
        List<Bird> birdList = new ArrayList<>();
        birdList.add(new Crow());
        birdList.add(new Ostrich());
        birdList.add(new Crow());
        letTheBirdsFly ( birdList );
    }

    public static void letTheBirdsFly ( List<Bird> birdList ){
        for (Bird b : birdList) {
            b.fly();
        }
    }
}
