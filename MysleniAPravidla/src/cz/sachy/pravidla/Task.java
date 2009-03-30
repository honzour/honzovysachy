package cz.sachy.pravidla;

import java.util.Iterator;
import java.util.Vector;

import cz.sachy.mysleni.HodnotaPozice;

public class Task {
	// End game constants
	public static final int NO_END = 0;
	
	public static final int WHITE_WINS_MAT = 1;
	
	public static final int BLACK_WINS_MAT = 11;
	
	public static final int DRAW_WHITE_IN_STALEMATE = 21;
	public static final int DRAW_BLACK_IN_STALEMATE = 22;
	public static final int DRAW_MATERIAL = 23;
	public static final int DRAW_50_MOVES = 24;
	public static final int DRAW_3_REPETITION = 25;

	// Move constants
	  public static final int MBRoch = (7<<13);
	  public static final int VBRoch = ((7<<13)|(1<<11));
	  public static final int MCRoch = (15<<12);
	  public static final int VCRoch = (31<<11);
	
	// End of game type
	public int mEnd;
	
	public Vector mPartie;
	  
	public int mIndexVPartii;
	  
	public ZasobnikTahu mZasobnik;
	  
	  
	public Vector zasobnik;
	  
	public int indexVZasobniku;
	  
	int index;
	
	public Hash hashF;
	
	public Pozice board;
	public Task() {
		board = new Pozice();
	    mIndexVPartii = -1;
	    indexVZasobniku = -1;
	    mPartie = new Vector();
	    zasobnik = new Vector();
	    mZasobnik = new ZasobnikTahu();
	    hashF = new Hash();
	}
	
	public String getEndOfGameString(int end) {
		if (end == 0) return "";
		String s = new String();
		if (end < 10) s = "White wins";
		else if (end < 20) s = "Black wins";
		else s = "Draw";
		switch (end) {
		case WHITE_WINS_MAT: s += ", black checkmated"; break;
		case BLACK_WINS_MAT: s += ", white checkmated"; break;
		case DRAW_WHITE_IN_STALEMATE:
		case DRAW_BLACK_IN_STALEMATE: s += ", stalemate"; break;
		case DRAW_MATERIAL: s += ", material"; break;
		case DRAW_50_MOVES: s += ", 50 moves rule"; break;
		case DRAW_3_REPETITION: s += ", 3 times repetition"; break;
		}
		return s;
	}
	
	public int getEndOfGame() {
		int i, bbs, bcs, cbs, ccs, cj, bj;
		
		 Vector v = nalezTahy();
		 
		 if (v.isEmpty()) {
			 if (board.sach()) return (board.bily ? BLACK_WINS_MAT : WHITE_WINS_MAT); 
			 return (board.bily ? DRAW_WHITE_IN_STALEMATE : DRAW_BLACK_IN_STALEMATE);
		 }

/*		 switch(Remiza50Nebo3(uloha))
		  {
		  case 50:uloha->KonecPartie=Remis50;break;
		  case 3: uloha->KonecPartie=Remis3;break;
		 }
		 if(uloha->KonecPartie)return; */
		 bbs = bcs = cbs = ccs = cj = bj = 0;
		 for(i = Pozice.a1; i <= Pozice.h8; i++) {
			 switch (board.sch[i]) {
			 case 1: return NO_END;
			 case 4: return NO_END;
			 case 5: return NO_END;
			 case -1: return NO_END;
			 case -4: return NO_END;
			 case -5: return NO_END;
			 case 2: bj++; break;
			 case -2: cj++; break ;
			 case 3: if ((((i/10)+(i%10))&1) != 0) bbs++; else bcs++; break;
			 case -3: if ((((i/10)+(i%10))&1) != 0) cbs++; else ccs++; break;
			 }
		 }
		 if ((i = bbs + bcs + cbs + ccs + cj + bj) <= 1 || i == bbs + cbs || i == bcs + ccs)
			 return DRAW_MATERIAL;
		 /*Je-li zadna nebo jen jedna lehka figura nebo
		  jsou na sachovnici jen strelci jedne barvy, nebude mat*/
		return NO_END;
	}
	
	public void tahniZpet(int tah, boolean globalne, ZobrazPole zobrazPole) {
		 
		int odkud, kam;
		   ZasobnikStruct z;
		   if (globalne) {
		     z = (ZasobnikStruct) mPartie.elementAt(mIndexVPartii--);
		   } else {
		     z = (ZasobnikStruct) zasobnik.elementAt(indexVZasobniku--);
		   }
		   board.mimoch = z.mimoch;
		   board.roch = z.roch;
		   board.bily = !board.bily;
		   
		   if((tah >> 15) == 0) /* Normalni tah*/
		    {
		     kam = tah & 127;
		     odkud = tah >> 7;
		board.sch[odkud] = board.sch[kam];
		board.sch[kam] = z.brani;
		     if (zobrazPole != null) {
		       zobrazPole.zobrazPole(odkud);
		       zobrazPole.zobrazPole(kam);
		     }
		     return;
		  }

		   /* Nenormalni tah

		      Mala bila rosada*/
		   if (tah == MBRoch) {
			   board.sch[Pozice.e1] = 6;
			   board.sch[Pozice.g1] = 0;
			   board.sch[Pozice.h1] = 4;
			   board.sch[Pozice.f1] = 0;
		     if (zobrazPole != null) {
		       zobrazPole.zobrazPole(Pozice.e1);
		       zobrazPole.zobrazPole(Pozice.f1);
		       zobrazPole.zobrazPole(Pozice.g1);
		       zobrazPole.zobrazPole(Pozice.h1);
		     }
		     return;
		   }
		  
		   /*Velka bila rosada*/
		   if (tah == VBRoch) {
			   board.sch[Pozice.e1] = 6;
			   board.sch[Pozice.c1] = 0;
			   board.sch[Pozice.a1] = 4;
			   board.sch[Pozice.d1] = 0;
		     if (zobrazPole != null) {
		       zobrazPole.zobrazPole(Pozice.e1);
		       zobrazPole.zobrazPole(Pozice.d1);
		       zobrazPole.zobrazPole(Pozice.c1);
		       zobrazPole.zobrazPole(Pozice.a1);
		     }
		     return;
		   }
		   
		  /*Mala cerna rosada*/
		   if (tah==MCRoch) {
		     board.sch[Pozice.e8] = -6;
		     board.sch[Pozice.g8] = 0;
		     board.sch[Pozice.h8] = -4;
		     board.sch[Pozice.f8] = 0;
		     if (zobrazPole != null) {
		       zobrazPole.zobrazPole(Pozice.e8);
		       zobrazPole.zobrazPole(Pozice.f8);
		       zobrazPole.zobrazPole(Pozice.g8);
		       zobrazPole.zobrazPole(Pozice.h8);
		     }
		     return;
		   }
		   /*Velka cerna rosada*/
		   if (tah == VCRoch) {
		     board.sch[Pozice.e8] = -6;
		     board.sch[Pozice.c8] = 0;
		     board.sch[Pozice.a8] = -4;
		     board.sch[Pozice.d8] = 0;
		     if (zobrazPole != null) {
		       zobrazPole.zobrazPole(Pozice.e8);
		       zobrazPole.zobrazPole(Pozice.d8);
		       zobrazPole.zobrazPole(Pozice.c8);
		       zobrazPole.zobrazPole(Pozice.a8);
		     }
		     return;
		   }
		  
		   /*Promena bileho pesce*/
		  if ((tah>>12)==12)
		   {odkud=Pozice.a7+((tah>>7)&7);
		    kam=Pozice.a8+((tah>>4)&7);
		    board.sch[odkud]=1;
		    board.sch[kam]=z.brani;
		    return;
		   }

		   /*Promena cerneho pesce*/
		  if ((tah>>12)==13)
		   {
		    odkud=Pozice.a2+((tah>>7)&7);
		    kam=Pozice.a1+((tah>>4)&7);
		    board.sch[odkud]=-1;
		    board.sch[kam]=z.brani;
		    return;
		   }
		  /* Brani mimochodem (nic jineho to uz byt nemuze)*/
		  tah&=0x3fff; /* odstraneni prvnich dvou bitu, aby se lepe siftovalo*/
		  kam=(tah&127);
		  odkud=(tah>>7);
		  board.sch[kam]=0;
		  if(odkud<kam)
		   {board.sch[kam-10]=-1; /* to hraje bily*/
		    board.sch[odkud]=1;
		    }
		  else
		  {board.sch[kam+10]=1;  /* cerny */
		   board.sch[odkud]=-1;
		   }
		  } 
		  
		  public void tahni(int tah, boolean globalne,
		      boolean ukousniKonec, ZobrazPole zobrazPole) {
		    
		     int odkud,kam;
		     byte co;
		     
		     push(globalne, ukousniKonec, tah);
		     board.mimoch = 0; /*Vetsina tahu neni pescem o 2, pokud ano, osetri se*/
		     board.bily = !board.bily;
		     
		     if ((tah>>15) == 0) /* Normalni tah*/
		      {kam = tah&127;
		       odkud = tah>>7;
		       if (/* bud cerny tahne pescem o 2*/
		         odkud - kam == 20 && board.sch[odkud] == -1
		                   /* a bily pesec ciha */
		           && (board.sch[kam + 1] == 1 || board.sch[kam - 1] == 1)
		           /* nebo bily tahne pescem o 2 */
		           || odkud - kam == -20 && board.sch[odkud] == 1
		                   /* a cerny pesec ciha */
		           && (board.sch[kam + 1] == -1 || board.sch[kam - 1] == -1))

		         board.mimoch = (byte) kam;
		    /* Niceni rosad
		      Pozn.: nejde dat vsude 'else', protoze napr. Va1xa8 nici obe velke rosady*/
		       if (odkud == Pozice.e1) board.roch &= 12; else /* 1100b*/
		       if (odkud == Pozice.e8) board.roch &= 3; else /* 0011b*/
		     { if (kam == Pozice.a1 || odkud == Pozice.a1) board.roch &= 13;/*1101b*/
		       if (kam == Pozice.h1 || odkud == Pozice.h1) board.roch &= 14;/*1110b*/
		       if (kam == Pozice.a8 || odkud == Pozice.a8) board.roch &= 7; /*0111b*/
		       if (kam == Pozice.h8 || odkud == Pozice.h8) board.roch &= 11;/*1011b*/
		     }
		   /* Ulozim si sebranou figuru*/
		    push(board.sch[kam], globalne);
		    /* zakladni rutina normalniho tahu:*/
		    board.sch[kam] = board.sch[odkud];
		    board.sch[odkud] = 0;
		    if (zobrazPole != null) {
		      zobrazPole.zobrazPole(odkud);
		      zobrazPole.zobrazPole(kam);
		    }
		    if (globalne) mEnd = getEndOfGame();
		    return;
		    }
		     /* Nenormalni tah

		        Mala bila rosada*/
		     if (tah == MBRoch)
		      {board.sch[Pozice.e1]=0;
		       board.sch[Pozice.g1]=6;
		       board.sch[Pozice.h1]=0;
		       board.sch[Pozice.f1]=4;
		       board.roch&=12;
		       push((byte)0, globalne);
		       if (zobrazPole != null) {
		         zobrazPole.zobrazPole(Pozice.e1);
		         zobrazPole.zobrazPole(Pozice.f1);
		         zobrazPole.zobrazPole(Pozice.g1);
		         zobrazPole.zobrazPole(Pozice.h1);
		       }
		       if (globalne) mEnd = getEndOfGame();
		       return;}
		     /*Velka bila rosada*/
		     if (tah == VBRoch)
		      {board.sch[Pozice.e1]=0;
		       board.sch[Pozice.c1]=6;
		       board.sch[Pozice.a1]=0;
		       board.sch[Pozice.d1]=4;
		       board.roch&=12;
		       push((byte)0, globalne);
		       if (zobrazPole != null) {
		         zobrazPole.zobrazPole(Pozice.a1);
		         zobrazPole.zobrazPole(Pozice.c1);
		         zobrazPole.zobrazPole(Pozice.d1);
		         zobrazPole.zobrazPole(Pozice.e1);
		       }
		       if (globalne) mEnd = getEndOfGame();
		       return;}
		    /*Mala cerna rosada*/
		     if (tah==MCRoch)
		      {board.sch[Pozice.e8]=0;
		       board.sch[Pozice.g8]=-6;
		       board.sch[Pozice.h8]=0;
		       board.sch[Pozice.f8]=-4;
		       board.roch&=3;
		       push((byte)0, globalne);
		       if (zobrazPole != null) {
		         zobrazPole.zobrazPole(Pozice.e8);
		         zobrazPole.zobrazPole(Pozice.f8);
		         zobrazPole.zobrazPole(Pozice.g8);
		         zobrazPole.zobrazPole(Pozice.h8);
		       }
		       if (globalne) mEnd = getEndOfGame();
		       return;}
		     /*Velka cerna rosada*/
		     if (tah==VCRoch)
		      {board.sch[Pozice.e8]=0;
		       board.sch[Pozice.c8]=-6;
		       board.sch[Pozice.a8]=0;
		       board.sch[Pozice.d8]=-4;
		       board.roch&=3;
		       push((byte)0, globalne);
		       if (zobrazPole != null) {
		         zobrazPole.zobrazPole(Pozice.a8);
		         zobrazPole.zobrazPole(Pozice.c8);
		         zobrazPole.zobrazPole(Pozice.d8);
		         zobrazPole.zobrazPole(Pozice.e8);
		       }
		       if (globalne) mEnd = getEndOfGame();
		       return;}
		     /*Promena bileho pesce*/
		    if ((tah>>12)==12)
		     {odkud=(Pozice.a7+((tah>>7)&7));
		      kam=(Pozice.a8+((tah>>4)&7));
		      co=(byte)(2+((tah>>10)&3));
		      /* Ulozim si, co jsem sebral */
		      push(board.sch[kam], globalne);
		      board.sch[odkud]=0;
		      board.sch[kam]=co;
		      if (kam==Pozice.a8) /* meni pesce na a8, mohl sezrat vez => rosady...*/
		       board.roch&=7; /*0111b*/
		      if (kam==Pozice.h8) /* meni pesce na h8, mohl sezrat vez => rosady...*/
		       board.roch&=11; /*1011b */
		      if (zobrazPole != null) {
		        zobrazPole.zobrazPole(odkud);
		        zobrazPole.zobrazPole(kam);
		      }
		      if (globalne) mEnd = getEndOfGame();
		      return;
		     }
		     /*Promena cerneho pesce*/
		    if ((tah>>12)==13)
		     {
		     odkud=(byte)(Pozice.a2+((tah>>7)&7));
		      kam=(byte)(Pozice.a1+((tah>>4)&7));
		      co=(byte)(-(2+((tah>>10)&3)));

		      /* Ulozim si, co jsem sebral */
		      push(board.sch[kam], globalne);
		      board.sch[odkud]=0;
		      board.sch[kam]=co;
		      if (kam==Pozice.a1) /* meni pesce na a1, mohl sezrat vez => rosady...*/
		        board.roch&=13; /*1101b*/
		       if (kam==Pozice.h1) /* meni pesce na h1, mohl sezrat vez => rosady...*/
		        board.roch&=14; /*1110b*/
		       if (zobrazPole != null) {
		         zobrazPole.zobrazPole(odkud);
		         zobrazPole.zobrazPole(kam);
		       }
		       if (globalne) mEnd = getEndOfGame();
		       return;
		      }
		     /* Brani mimochodem (nic jineho to uz byt nemuze)*/
		     tah&=0x3fff; /* odstraneni prvnich dvou bitu, aby se lepe siftovalo*/
		     kam=(tah&127);
		     odkud=(tah>>7);
		     if(odkud<kam)
		      {board.sch[kam-10]=0;
		       push((byte)-1, globalne);
		     }
		       /* to hral bily*/
		     else
		      {
		    		 board.sch[kam+10]=0;
		    	 	 push((byte)1, globalne);
		     }
		       /* cerny*/
		     board.sch[kam] = board.sch[odkud];
		     board.sch[odkud] = 0;
		     if (zobrazPole != null) {
		       zobrazPole.zobrazPole(odkud);
		       zobrazPole.zobrazPole(kam);
		       zobrazPole.zobrazPole(kam + 10);
		       zobrazPole.zobrazPole(kam - 10);
		     }
		     if (globalne) mEnd = getEndOfGame();
		     return;
		  }

		  protected void push(byte brani,boolean globalne) {
			    if (globalne) {
			      ((ZasobnikStruct)mPartie.elementAt(mIndexVPartii)).brani = brani;
			    } else {
			      ((ZasobnikStruct)zasobnik.elementAt(indexVZasobniku)).brani = brani;
			    }
			  }
			  
			  protected void push(boolean globalne, boolean ukousniKonec, int tah) {
			    if (globalne) {
			      mIndexVPartii++;
			      if (mIndexVPartii <= mPartie.size()) {
			        mPartie.add(new ZasobnikStruct(board.roch, board.mimoch, tah));
			      } else {
			        ((ZasobnikStruct) mPartie.elementAt(mIndexVPartii)).set(board.roch, board.mimoch, tah);
			        if (ukousniKonec && mPartie.size() > mIndexVPartii + 1) {
			          mPartie.setSize(mIndexVPartii + 1);
			        }
			      }
			    } else {
			      indexVZasobniku++;
			      if (indexVZasobniku >= zasobnik.size()) {
			        zasobnik.add(new ZasobnikStruct(board.roch, board.mimoch, tah));
			      } else {
			        ((ZasobnikStruct) zasobnik.elementAt(indexVZasobniku)).set(board.roch, board.mimoch, tah);
			      }
			    }
			  }


public Vector nalezTahy() {
    Vector tahy = board.nalezPseudolegalniTahy2();
    Vector t = new Vector();
    Iterator i = tahy.iterator();
    while (i.hasNext()) {
      int tah = ((Integer) i.next()).intValue();
      tahni(tah, false, false, null);
      if (!board.sach(!board.bily)) {
        t.add(new Integer(tah));
      }
      tahniZpet(tah, false, null);
    }
    tahy = t;
    return t;
  }

public int getOdkud() {
	return mZasobnik.pos == 1 ? 0 : mZasobnik.hranice[mZasobnik.pos - 2];
  }
  
  public int getKam() {
	  return mZasobnik.hranice[mZasobnik.pos - 1];
  }
  private void zaradTah(int i, int j) {
	    mZasobnik.tahy[index] = (i << 7) | j;
	    mZasobnik.hodnoty[index] = HodnotaPozice.mStdCenyFigur[abs(board.sch[j])];
	    index++;
	}
	  
	  private void zaradMimochodem(int i, int j) {
		  mZasobnik.tahy[index] =  (1<<15) | ((i)<<7) | (j);
		  index++;
	  }
	  
	  private void zaradBilouPromenu(int p1, int p2, int fig) {
		  mZasobnik.tahy[index] =  ((3<<14)|(fig<<10)|((p1-Pozice.a7)<<7)|((p2-Pozice.a8)<<4));
		  if (mZasobnik.tahy[index] < 0) {
			  System.out.println(toString());
		  }
		  index++;
	  }
	  
	  private void zaradCernouPromenu(int p1, int p2, int fig) {
		  mZasobnik.tahy[index] =  ((13<<12)|(fig<<10)|((p1-Pozice.a2)<<7)|((p2-Pozice.a1)<<4));
		  index++;
	  }
	  

	  private void dlouhaBilaFigura(int o1, int o2, int p) {
	    for(int j = o1; j <= o2; j++) {
	      for(int q = p + Pozice.mOfsety[j]; board.sch[q] <= 0; q += Pozice.mOfsety[j]) {
	        zaradTah(p, q);
	        if (board.sch[q] < 0) {
	          break;
	        }
	      }
	    }
	  }
	  
	  private void dlouhaCernaFigura(int o1, int o2, int p) {
	    for(int j = o1; j <= o2; j++) {
	      for(int q = p + Pozice.mOfsety[j]; board.sch[q] >= 0 && board.sch[q] < 7; q += Pozice.mOfsety[j]) {
	        zaradTah(p, q);
	        if (board.sch[q] > 0) {
	          break;
	        }
	      }
	    }
	  }
	  
	  private void dlouhaBilaFiguraBrani(int o1, int o2, int p) {
		    for(int j = o1; j <= o2; j++) {
		      for(int q = p + Pozice.mOfsety[j]; board.sch[q] <= 0; q += Pozice.mOfsety[j]) {
		        
		        if (board.sch[q] < 0) {
		        	zaradTah(p, q);
		          break;
		        }
		      }
		    }
		  }
		  
		  private void dlouhaCernaFiguraBrani(int o1, int o2, int p) {
		    for(int j = o1; j <= o2; j++) {
		      for(int q = p + Pozice.mOfsety[j]; board.sch[q] >= 0 && board.sch[q] < 7; q += Pozice.mOfsety[j]) {
		        
		        if (board.sch[q] > 0) {
		        	zaradTah(p, q);
		          break;
		        }
		      }
		    }
		  }
	  
	  
	  private void zaradRosadu(int jakou) {
		  mZasobnik.tahy[index] =  jakou;
		  index++;
	  }


public void nalezPseudolegalniTahyZasobnik() {
	  int j, i; /*promenne pro for cykly*/
	  if (mZasobnik.pos == 0) 
		  index = 0;
	  else 
		  index = mZasobnik.hranice[mZasobnik.pos - 1];
	    
	    if (board.bily) {
	     for (i = Pozice.a1; i <= Pozice.h8; i++)
	      {if (board.sch[i] < 1 || board.sch[i] > 6) continue;
	       switch (board.sch[i]) {
	        case 1 : /*pesec*/
	         if (i < Pozice.a7) /*tedy nehrozi promena*/ {
	           if (board.sch[i + 11] < 0) zaradTah(i, i + 11);
	           if (board.sch[i + 9] < 0) zaradTah(i, i + 9);
	           if (board.sch[i + 10] == 0) {
	             zaradTah(i, i + 10);
	             if (i <= Pozice.h2 && board.sch[i + 20] == 0) zaradTah(i,i+20);
	           } /* pescem o 2*/
	           if (board.mimoch == i + 1) zaradMimochodem(i, i + 11); else
	           if (board.mimoch == i - 1) zaradMimochodem(i, i + 9);
	         }
	        else /* i>=a7 => promeny pesce*/
	         {if (board.sch[i + 10] == 0) for(j=3;j>=0;j--) zaradBilouPromenu(i, i + 10, j);
	          if (board.sch[i + 11] < 0) for(j=3;j>=0;j--) zaradBilouPromenu(i, i + 11, j);
	          if (board.sch[i + 9] < 0) for(j=3;j>=0;j--) zaradBilouPromenu(i, i + 9, j);
	         }
	      break;
	     case 2: /* kun*/
	      for (j = 8; j <= 15; j++)
	        if ((board.sch[i + Pozice.mOfsety[j]]) <=0 ) zaradTah(i,i + Pozice.mOfsety[j]);
	      break;
	     case 3: /* strelec*/
	       dlouhaBilaFigura(4, 7, i);
	     break;
	     case 4: /* vez*/
	       dlouhaBilaFigura(0, 3, i);
	     break;
	     case 5: /* dama*/
	       dlouhaBilaFigura(0, 7, i);
	     break;
	     case 6: /* kral*/
	       for (j = 0; j <= 7; j++) 
	         if ((board.sch[i + Pozice.mOfsety[j]]) <= 0) zaradTah(i, i + Pozice.mOfsety[j]);
	       if (i == Pozice.e1 && ((board.roch & 1) != 0) && (board.sch[i + 1] == 0) && (board.sch[i + 2] == 0) && (board.sch[Pozice.h1] == 4)
	         && !board.ohrozeno(i + 1, false) && !board.ohrozeno(i, false))  {
	         zaradRosadu(MBRoch);
	       }
	       if (i == Pozice.e1 && ((board.roch & 2) != 0) && (board.sch[i - 1] == 0) && (board.sch[i - 2] == 0) && (board.sch[Pozice.a1] == 4)
	         && !board.ohrozeno(i - 1, false) && !board.ohrozeno(i, false))  {
	         zaradRosadu(VBRoch);
	       }
	       break; /* od krale */
	     }/* od switch*/
	   } /* od for cyklu*/
	 } /* od hraje bily*/
	    else
	     {
	     for (i = Pozice.a1; i <= Pozice.h8; i++) {
	       if (board.sch[i] >=0 ) continue;
	       switch (board.sch[i]) {
	         case -1 : /*pesec*/
	         if (i>Pozice.h2) /*tedy nehrozi promena*/ {
	           if (board.sch[i - 11] > 0 && board.sch[i - 11] < 7)
	             zaradTah(i, i - 11);
	           if (board.sch[i - 9] > 0 && board.sch[i - 9] < 7) 
	             zaradTah(i, i - 9);
	           if (board.sch[i - 10] == 0) /* policko pred pescem je volne*/ {
	             zaradTah(i, i - 10);
	             if (i >= Pozice.a7 && board.sch[i - 20] == 0)
	               zaradTah(i, i-20);
	             } /* pescem o 2*/
	             if (board.mimoch == i + 1) zaradMimochodem(i, i - 9); else
	             if (board.mimoch == i - 1) zaradMimochodem(i, i - 11);
	           } else /* i<=h2 => promeny pesce*/ {
	             if (board.sch[i - 10] == 0)
	               for(j = 3; j >= 0; j--)
	                 zaradCernouPromenu(i, i - 10, j);
	             if (board.sch[i - 11] > 0 && board.sch[i - 11] < 7)
	               for(j = 3; j >= 0; j--)
	                 zaradCernouPromenu(i, i - 11, j);
	             if (board.sch[i - 9] > 0 && board.sch[i - 9] < 7)
	               for(j = 3; j >= 0; j--)
	                 zaradCernouPromenu(i, i - 9, j);
	           }
	      break;
	     case -2: /* kun*/
	      for (j = 8; j <= 15; j++)
	        if (board.sch[i + Pozice.mOfsety[j]] >=0 && board.sch[i + Pozice.mOfsety[j]]  <7)
	          zaradTah(i, i + Pozice.mOfsety[j]);
	      break;
	     case -3: /* strelec*/
	       dlouhaCernaFigura(4, 7, i);
	       break;
	     case -4: /* vez*/
	       dlouhaCernaFigura(0, 3, i);
	       break;
	     case -5: /* dama*/
	       dlouhaCernaFigura(0, 7, i);
	       break;
	     case -6: /* kral*/
	       for (j = 0; j <= 7; j++)
	         if (board.sch[i + Pozice.mOfsety[j]] >= 0 && board.sch[i + Pozice.mOfsety[j]] < 7)
	           zaradTah(i, i + Pozice.mOfsety[j]);
	       if (i == Pozice.e8 && (board.roch & 4) != 0 && board.sch[Pozice.f8] == 0 && board.sch[Pozice.g8] == 0 && (board.sch[Pozice.h8] == -4)
	           && !board.ohrozeno(Pozice.e8, true) && !board.ohrozeno(Pozice.f8, true)) {
	         zaradRosadu(MCRoch);
	       }
	       if (i == Pozice.e8 && (board.roch & 8) != 0 && board.sch[Pozice.d8] == 0 && board.sch[Pozice.c8] == 0 && (board.sch[Pozice.a8] == -4)
	         && !board.ohrozeno(Pozice.e8, true) && !board.ohrozeno(Pozice.d8, true)){
	       zaradRosadu(VCRoch);
	      }
	      break;
	     }/* od switch*/
	    } /* od for cyklu*/
	    } /* od hraje cerny*/
	    mZasobnik.hranice[mZasobnik.pos] = index;
	    mZasobnik.pos++;
	    
	    
	    if (true) {
	    int maxPos;
	    int max;
	    int tmp;
	    for (i = (mZasobnik.pos == 1 ? 0 : mZasobnik.hranice[mZasobnik.pos - 2]); i < index - 1; i++) {
	    	maxPos = i;
	    	max = mZasobnik.hodnoty[i];
	    	for (j = i + 1; j < index; j++) {
	    		  if (mZasobnik.hodnoty[j] > max) {
	    			  maxPos = j;
	    			  max = mZasobnik.hodnoty[j];
	    		  }
	    	}
	    	if (maxPos != i) {
	    		mZasobnik.hodnoty[maxPos] = mZasobnik.hodnoty[i] ;
	    		mZasobnik.hodnoty[i] = max;
	    		tmp = mZasobnik.tahy[maxPos];
	    		mZasobnik.tahy[maxPos] = mZasobnik.tahy[i];
	    		mZasobnik.tahy[i] = tmp;
	    	}
	    }
	    }
	  }

public void nalezTahyZasobnik() {
	nalezPseudolegalniTahyZasobnik();
	int odkud = getOdkud();
	int kam = getKam();

	boolean jeSach = false;
	for (int i = odkud; i < kam; i++) {
	   int tah = mZasobnik.tahy[i];
	  tahni(tah, false, false, null);
	  if (board.sach(!board.bily)) {
		  mZasobnik.tahy[i] = 0;
		  jeSach = true;
	  }
	  tahniZpet(tah, false, null);
	}
	
	if (jeSach) {
		int indexOdkud = odkud;
		int indexKam = odkud;
		hlavni:
		while (indexOdkud < kam) {
			if (mZasobnik.tahy[indexKam] == 0) {
				while (mZasobnik.tahy[indexOdkud] == 0) { 
					indexOdkud++;
					if (indexOdkud == kam) {
						break hlavni; 
					}
				}
			} 
			mZasobnik.tahy[indexKam++] = mZasobnik.tahy[indexOdkud++];
		}
		mZasobnik.hranice[mZasobnik.pos - 1] = indexKam;
	}

}

public void nalezPseudolegalniBraniZasobnik() {
	  int j, i; /*promenne pro for cykly*/
	  if (mZasobnik.pos == 0) 
		  index = 0;
	  else 
		  index = mZasobnik.hranice[mZasobnik.pos - 1];
	    
	    if (board.bily) {
	     for (i = Pozice.a1; i <= Pozice.h8; i++)
	      {if (board.sch[i] < 1 || board.sch[i] > 6) continue;
	       switch (board.sch[i]) {
	        case 1 : /*pesec*/
	         if (i < Pozice.a7) /*tedy nehrozi promena*/ {
	           if (board.sch[i + 11] < 0) zaradTah(i, i + 11);
	           if (board.sch[i + 9] < 0) zaradTah(i, i + 9);
	           if (board.mimoch == i + 1) zaradMimochodem(i, i + 11); else
	           if (board.mimoch == i - 1) zaradMimochodem(i, i + 9);
	         }
	        else /* i>=a7 => promeny pesce*/
	         {if (board.sch[i + 10] == 0) for(j=3;j>=0;j--) zaradBilouPromenu(i, i + 10, j);
	          if (board.sch[i + 11] < 0) for(j=3;j>=0;j--) zaradBilouPromenu(i, i + 11, j);
	          if (board.sch[i + 9] < 0) for(j=3;j>=0;j--) zaradBilouPromenu(i, i + 9, j);
	         }
	      break;
	     case 2: /* kun*/
	      for (j = 8; j <= 15; j++)
	        if ((board.sch[i + Pozice.mOfsety[j]]) <0 ) zaradTah(i,i + Pozice.mOfsety[j]);
	      break;
	     case 3: /* strelec*/
	       dlouhaBilaFiguraBrani(4, 7, i);
	     break;
	     case 4: /* vez*/
	       dlouhaBilaFiguraBrani(0, 3, i);
	     break;
	     case 5: /* dama*/
	       dlouhaBilaFiguraBrani(0, 7, i);
	     break;
	     case 6: /* kral*/
	       for (j = 0; j <= 7; j++) 
	         if ((board.sch[i + Pozice.mOfsety[j]]) < 0) zaradTah(i, i + Pozice.mOfsety[j]);
	       
	       break; /* od krale */
	     }/* od switch*/
	   } /* od for cyklu*/
	 } /* od hraje bily*/
	    else
	     {
	     for (i = Pozice.a1; i <= Pozice.h8; i++) {
	       if (board.sch[i] >=0 ) continue;
	       switch (board.sch[i]) {
	         case -1 : /*pesec*/
	         if (i > Pozice.h2) /*tedy nehrozi promena*/ {
	           if (board.sch[i - 11] > 0 && board.sch[i - 11] < 7)
	             zaradTah(i, i - 11);
	           if (board.sch[i - 9] > 0 && board.sch[i - 9] < 7) 
	             zaradTah(i, i - 9);
	             
	             if (board.mimoch == i + 1) zaradMimochodem(i, i - 9); else
	             if (board.mimoch == i - 1) zaradMimochodem(i, i - 11);
	           } else /* i<=h2 => promeny pesce*/ {
	             if (board.sch[i - 10] == 0)
	               for(j = 3; j >= 0; j--)
	                 zaradCernouPromenu(i, i - 10, j);
	             if (board.sch[i - 11] > 0 && board.sch[i - 11] < 7)
	               for(j = 3; j >= 0; j--)
	                 zaradCernouPromenu(i, i - 11, j);
	             if (board.sch[i - 9] > 0 && board.sch[i - 9] < 7)
	               for(j = 3; j >= 0; j--)
	                 zaradCernouPromenu(i, i - 9, j);
	           }
	      break;
	     case -2: /* kun*/
	      for (j = 8; j <= 15; j++)
	        if (board.sch[i + Pozice.mOfsety[j]] > 0 && board.sch[i + Pozice.mOfsety[j]] < 7)
	          zaradTah(i, i + Pozice.mOfsety[j]);
	      break;
	     case -3: /* strelec*/
	       dlouhaCernaFiguraBrani(4, 7, i);
	       break;
	     case -4: /* vez*/
	       dlouhaCernaFiguraBrani(0, 3, i);
	       break;
	     case -5: /* dama*/
	       dlouhaCernaFiguraBrani(0, 7, i);
	       break;
	     case -6: /* kral*/
	       for (j = 0; j <= 7; j++)
	         if (board.sch[i + Pozice.mOfsety[j]] > 0 && board.sch[i + Pozice.mOfsety[j]] < 7)
	           zaradTah(i, i + Pozice.mOfsety[j]);

	      break;
	     }/* od switch*/
	    } /* od for cyklu*/
	    } /* od hraje cerny*/
	    mZasobnik.hranice[mZasobnik.pos] = index;
	    mZasobnik.pos++;
	    
		
	    
  	int maxPos;
  	int max;
  	int tmp;
  	for (i = (mZasobnik.pos == 1 ? 0 : mZasobnik.hranice[mZasobnik.pos - 2]); i < index - 1; i++) {
  		maxPos = i;
  		max = mZasobnik.hodnoty[i];
  		for (j = i + 1; j < index; j++) {
  			if (mZasobnik.hodnoty[j] > max) {
  				maxPos = j;
  				max = mZasobnik.hodnoty[j];
  			}
  		}
  		if (maxPos != i) {
  			mZasobnik.hodnoty[maxPos] = mZasobnik.hodnoty[i] ;
  			mZasobnik.hodnoty[i] = max;
  			tmp = mZasobnik.tahy[maxPos];
  			mZasobnik.tahy[maxPos] = mZasobnik.tahy[i];
  			mZasobnik.tahy[i] = tmp;
  		}
  	}
  }

public static int abs(int i) {
  if (i < 0) return -i;
  return i;
}

private static final int J_Nic = 0;
private static final int J_Radka = 1;
private static final int J_Sloupec = 2;

/* Je tah urcen jednoznacne ? (urcen je J_xxx) a tahy v uloze jsou nalezene */
private boolean jednoZnacny(Vector tahy, int tah, int urcen)
 {int odkud, kam;

 if (tah>>14 != 0) return true;/* Nenormalni tah je vzdy jednoznacny*/
  kam=tah&127;
  odkud=tah>>7;

  Iterator i = tahy.iterator();

  while (i.hasNext()) {
   int t = ((Integer)(i.next())).intValue();    
   if (((t>>14) == 0)&&        /* normalni tah */
       ((t&127)==kam)&&     /* na stejne policko */
       (odkud!=(t>>7)) &&   /* z jineho policka */
       ((board.sch[odkud])==(board.sch[t>>7])) /* stejnou figurkou */
       )
      switch(urcen){
        case J_Nic: return false;
        case J_Radka: if(odkud/10==(t>>7)/10)return false; break;
        case J_Sloupec: if(odkud%10==(t>>7)%10)return false; break;
      }
  }
  return true;
 } 

public String tah2Str(int tah) {
	    int odkud, kam;
	    StringBuffer s = new StringBuffer();

	    if ((tah >> 14) == 0) /* Normalni tah*/ {
	      kam = tah & 127;
	      odkud = tah >> 7;
	      //i = 0;
	    switch (abs(board.sch[odkud])) {
	      case 1:
	      if (board.sch[kam] != 0) s.append((char)('a' + (odkud - Pozice.a1) % 10));
	      break;
	      case 2: s.append('N'); break;
	      case 3: s.append('B'); break;
	      case 4: s.append('R'); break;
	      case 5: s.append('Q'); break;
	      case 6: s.append('K'); break;
	    }
	   if (abs(board.sch[odkud]) != 1) {
	      s.append((char)((odkud - Pozice.a1) % 10 + 'a'));
	      s.append((char)((odkud - Pozice.a1) / 10 + '1'));
	   }
	   if (board.sch[kam] != 0) s.append('x');
	   s.append((char)((kam - Pozice.a1)%10 + 'a'));
	   s.append((char)((kam - Pozice.a1)/10 + '1'));
	     
	    return new String(s);
	  }
	  /* Nenormalni tah
	     Mala rosada*/
	  if (tah==MBRoch || tah==MCRoch) return "O-O";
	  /*Velka rosada*/
	  if (tah==VBRoch || tah==VCRoch) return "O-O-O";

	  /*Promena bileho pesce*/
	 if ((tah>>12)==12 || (tah>>12)==13 ) {
	 if ((tah>>12)==12)
	  {odkud=Pozice.a7+((tah>>7)&7);
	   kam=Pozice.a8+((tah>>4)&7);}
	  else
	  {odkud=Pozice.a2+((tah>>7)&7);
	   kam=Pozice.a1+((tah>>4)&7);}
	   s.append((char)((odkud-Pozice.a1)%10 + 'a'));
	   if (board.sch[kam] != 0) {s.append('x'); s.append((char)((kam-Pozice.a1)%10 + 'a'));}
	   s.append((char)((kam-Pozice.a1)/10 + '1'));
	   switch((tah>>10)&3){
	   case 0: s.append('N'); break;
	   case 1: s.append('B'); break;
	   case 2: s.append('R'); break;
	   case 3: s.append('Q'); break;
	   }
	   return new String(s);
	  }
	 /* Brani mimochodem (nic jineho to uz byt nemuze)*/
	 tah&=0x3fff; /* odstraneni prvnich dvou bitu, aby se lepe siftovalo*/
	 kam=tah&127;
	 odkud=tah>>7;
	 s.append((char)((odkud-Pozice.a1)%10 + 'a'));
	/* s[i++]=(odkud-a1)/10 + '1';*/
	 s.append('x');
	 s.append((char)((kam-Pozice.a1)%10 + 'a'));
	 s.append((char)((kam-Pozice.a1)/10 + '1'));
	 return new String(s);
	}

public String tah2Str(Vector tahy, int tah) {
  int odkud, kam;
  StringBuffer s = new StringBuffer();

  if ((tah >> 14) == 0) /* Normalni tah*/ {
    kam = tah & 127;
    odkud = tah >> 7;
    //i = 0;
  switch (abs(board.sch[odkud])) {
    case 1:
    if (board.sch[kam] != 0) s.append((char)('a' + (odkud - Pozice.a1) % 10));
    break;
    case 2: s.append('J'); break;
    case 3: s.append('S'); break;
    case 4: s.append('V'); break;
    case 5: s.append('D'); break;
    case 6: s.append('K'); break;
  }
 if (abs(board.sch[odkud]) != 1) {
   if (!jednoZnacny(tahy, tah, J_Nic)) /* Zkusim Da1 */
  {    /* Tak Dha1 */
    if (jednoZnacny(tahy, tah, J_Sloupec)) s.append((char)((odkud - Pozice.a1)%10 + 'a'));
     else /* Tak D1a1 */
      if (jednoZnacny(tahy, tah, J_Radka)) s.append((char)((odkud - Pozice.a1)/10 + '1'));
      else /* Tak teda Dh1a1 (nutne pokud jsou 3 damy na h1, h8 a a8)*/
       {
         s.append((char)((odkud - Pozice.a1)%10 + 'a'));
         s.append((char)((odkud - Pozice.a1)/10 + '1'));
       }
   }
 }
 if (board.sch[kam] != 0) s.append('x');
 s.append((char)((kam - Pozice.a1)%10 + 'a'));
 s.append((char)((kam - Pozice.a1)/10 + '1'));
   
  return new String(s);
}
/* Nenormalni tah
   Mala rosada*/
if (tah==MBRoch || tah==MCRoch) return "O-O";
/*Velka rosada*/
if (tah==VBRoch || tah==VCRoch) return "O-O-O";

/*Promena bileho pesce*/
if ((tah>>12)==12 || (tah>>12)==13 ) {
if ((tah>>12)==12)
{odkud=Pozice.a7+((tah>>7)&7);
 kam=Pozice.a8+((tah>>4)&7);}
else
{odkud=Pozice.a2+((tah>>7)&7);
 kam=Pozice.a1+((tah>>4)&7);}
 s.append((char)((odkud-Pozice.a1)%10 + 'a'));
 if (board.sch[kam] != 0) {s.append('x'); s.append((char)((kam-Pozice.a1)%10 + 'a'));}
 s.append((char)((kam-Pozice.a1)/10 + '1'));
 switch((tah>>10)&3){
 case 0: s.append('J'); break;
 case 1: s.append('S'); break;
 case 2: s.append('V'); break;
 case 3: s.append('D'); break;
 }
 return new String(s);
}
/* Brani mimochodem (nic jineho to uz byt nemuze)*/
tah&=0x3fff; /* odstraneni prvnich dvou bitu, aby se lepe siftovalo*/
kam=tah&127;
odkud=tah>>7;
s.append((char)((odkud-Pozice.a1)%10 + 'a'));
/* s[i++]=(odkud-a1)/10 + '1';*/
s.append('x');
s.append((char)((kam-Pozice.a1)%10 + 'a'));
s.append((char)((kam-Pozice.a1)/10 + '1'));
s.append(' ');
s.append('e');
s.append('.');
s.append('p');
s.append('.');
return new String(s);
} 
/**
 * Je mezi nalezenymi tahi nejaky vedouci z odkud ?
 * @param odkud
 * @return
 */
public boolean JeTam1(Vector tahy, int odkud) {
  //int p, k;
  
  Iterator ti = tahy.iterator(); 
  while (ti.hasNext()) {
    int t = ((Integer)ti.next()).intValue(); 
    if((t>>15) == 0) /* Normalni tah*/  if  (odkud==t>>7)  return true; else continue;
    if ((t==MBRoch || t==VBRoch)) if (odkud==Pozice.e1) return true; else continue;
    if ((t==MCRoch || t==VCRoch)) if (odkud==Pozice.e8) return true; else continue;
  /*Promena bileho pesce*/
    if ((t>>12)==12) if (odkud == Pozice.a7+((t>>7)&7)) return true; else continue;
  /*Promena cerneho pesce*/
    if ((t>>12)==13) if (odkud == Pozice.a2+((t>>7)&7)) return true; else continue;
  /* Brani mimochodem (nic jineho to uz byt nemuze)*/
    if ((t&0x3fff)>>7 == odkud) return true; else continue;
  }
  return false;
}

public boolean JeTam2(Vector tahy, int odkud, int kam) {
  Iterator ti = tahy.iterator(); 
  while (ti.hasNext()) {
    int t = ((Integer)ti.next()).intValue();
    // Normalni tah
    if((t>>15) == 0)  if(kam==(t&127) && odkud==(t>>7)) return true; else continue;
/*Ro��dy*/
    if (t==MBRoch) if(odkud==Pozice.e1 && kam==Pozice.g1) return true; else continue;
    if (t==VBRoch) if(odkud==Pozice.e1 && kam==Pozice.c1) return true; else continue;
    if (t==MCRoch) if(odkud==Pozice.e8 && kam==Pozice.g8) return true; else continue;
    if (t==VCRoch) if(odkud==Pozice.e8 && kam==Pozice.c8) return true; else continue;
  /*Promena bileho pesce*/
    if ((t>>12)==12)
      if(odkud==Pozice.a7+((t>>7)&7) && kam==Pozice.a8+((t>>4)&7)) return true; else continue;
  /*Promena cerneho pesce*/
    if ((t>>12)==13)
      if(odkud==Pozice.a2+((t>>7)&7) && kam==Pozice.a1+((t>>4)&7)) return true; else continue;
   /* Brani mimochodem (nic jineho to uz byt nemuze)*/
    t&=0x3fff; /* odstraneni prvnich dvou bitu, aby se lepe siftovalo*/
    if(kam==(t&127) && odkud==(t>>7)) return true; else continue;
  } /* konec while cyklu*/
  return false;
}

public int DoplnTah(Vector tahy, int odkud, int kam) {
  Iterator ti = tahy.iterator(); 
  while (ti.hasNext()) {
    int t = ((Integer)ti.next()).intValue();
     /* Normalni tah*/
   if((t>>15) == 0)  if(kam==(t&127) && odkud==(t>>7)) return t; else continue;
/*Rosady*/
   if (t==MBRoch) if(odkud==Pozice.e1 && kam==Pozice.g1)  return t; else continue;
   if (t==VBRoch) if(odkud==Pozice.e1 && kam==Pozice.c1)  return t; else continue;
   if (t==MCRoch) if(odkud==Pozice.e8 && kam==Pozice.g8)  return t; else continue;
   if (t==VCRoch) if(odkud==Pozice.e8 && kam==Pozice.c8)  return t; else continue;
  /*Promena bileho pesce*/
   if ((t>>12)==12)
     if(odkud==Pozice.a7+((t>>7)&7) && kam==Pozice.a8+((t>>4)&7))
       return(t&(0xFFFF^(3<<10)))|(/*MessageBox.messageBox("Prom�na p�ce", "Vyberte figuru", MessageBox.MB_PROMENA3)*/3<<10);
     else continue;
  /*Promena cerneho pesce*/
if ((t>>12)==13)
 if(odkud==Pozice.a2+((t>>7)&7) && kam==Pozice.a1+((t>>4)&7))
  return(t&(0xFFFF^(3<<10)))|(/*MessageBox.messageBox("Prom�na p�ce", "Vyberte figuru", MessageBox.MB_PROMENA)*/3<<10);
 else continue;
/* Brani mimochodem (nic jineho to uz byt nemuze)*/
if(kam==(t&0x3fff&127) && odkud==((t&0x3fff)>>7)) return t; else continue;
} /* konec while cyklu*/
 return 0;
 }
  
  }