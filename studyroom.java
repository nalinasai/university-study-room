import java.io.*;
import java.util.*;

class Rooms{
    String room_number;
    int total_seats;
    int available_seats;
    List<Integer> seats_int;

    Rooms(String room_number,int total_seats,int available_seats,List<Integer> seats_int){
        this.room_number = room_number;
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.seats_int = seats_int;
    }
}

public class studyroom{
    private static final String filename = "study.csv";
    private static Map<String,Rooms> roomsmap = new HashMap<>();

    public static void main(String args[]){
        loadthedetails();

        Scanner scan = new Scanner(System.in);

        System.out.println("WELCOME TO THE UNIVERSITY OF MORATUWA LIBRARY.");

        while(true){
            System.out.print("which number study room you want: Enter 1 to 10: ");
            String input_room_num = scan.nextLine();
            Rooms select_room = roomsmap.get(input_room_num);
            if(select_room.total_seats>select_room.available_seats){
                System.out.print("Here are the seat numbers available.");
                for(int j=0; j<select_room.seats_int.size(); j++){
                    System.out.print(select_room.seats_int.get(j)+" ");
                }
                System.out.print("Do you want to pick a seat? yes/no: ");
                String yes_or_no = scan.nextLine();
                if(yes_or_no.equalsIgnoreCase("yes")){
                    break;
                }
                else{
                    System.out.print("Do you want to exit or take a another room? if exit yes: take another room no: ");
                    String exit_or_room = scan.nextLine();
                    if(exit_or_room.equalsIgnoreCase("yes")){
                        return;
                    }
                    else{
                        continue;
                    }
                }
                
            }
            else{
                System.out.println("There is no available seats in this room. you can pick another room.");
            }

        }
        System.out.println("selsected the room and seat.");
        
    }

    private static void loadthedetails(){
        
        try(BufferedReader bw = new BufferedReader(new FileReader(filename))){
            bw.readLine();
            String line;
            while((line=bw.readLine())!=null){
                String[] details = line.split(",");
                String room_number = details[0];
                int total_seats = Integer.parseInt(details[1]);
                int available_seats = Integer.parseInt(details[2]);
                String[] seats = details[3].split(":");
                List<Integer> seats_int = new ArrayList<>();
                for(String seat: seats){
                    seats_int.add(Integer.parseInt(seat.trim()));
                }

                Rooms room = new Rooms(room_number,total_seats,available_seats,seats_int);
                roomsmap.put(room_number,room);

            }
            
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}

//room number,total seats,available seats,chair numbers