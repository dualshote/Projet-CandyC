/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.awt.Color;

/**
 *
 * @author dualshote
 */
public class Forme {
    
    Color c;
    int numColor;
    
    public Forme(int numeroAleatoire){
        switch(numeroAleatoire){
            case 0: this.c = Color.blue;
                this.numColor = 0;
                break;
            case 1: this.c = Color.yellow;
                this.numColor = 1;
                break;
            case 2: this.c = Color.red;
                this.numColor = 2;
                break;
            default: this.c = Color.black;
                this.numColor = 0;
                break;
        }
    }
    
    public int getForme(){
        return numColor;
    }
    
    public boolean equals(Forme forme){
        if(this.numColor == forme.getForme()){
            return true;
        }
        else{
            return false;
        }
    }
}