package pwn.noobs.trouserstreak.modules;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import pwn.noobs.trouserstreak.Trouser;

public class VoiderPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<String> block = sgGeneral.add(new StringSetting.Builder()
            .name("Block to be used for /fill")
            .description("What is created.")
            .defaultValue("air")
            .build());
    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
        .name("radius")
        .description("radius")
        .defaultValue(45)
        .sliderRange(1, 90)
        .build());
    private final Setting<Integer> maxheight = sgGeneral.add(new IntSetting.Builder()
        .name("maxheight")
        .description("maxheight")
        .defaultValue(128)
        .sliderRange(64, 319)
        .build());
    private final Setting<Integer> minheight = sgGeneral.add(new IntSetting.Builder()
        .name("minheight")
        .description("minheight")
        .defaultValue(-64)
        .sliderRange(-64, 128)
        .build());
    public final Setting<Boolean> threebythree = sgGeneral.add(new BoolSetting.Builder()
            .name("VoiderBot3x3")
            .description("Runs voider nine times in a 3x3 grid pattern to replace a whole lot more")
            .defaultValue(false)
            .build()
    );
    public final Setting<Boolean> tpfwd = sgGeneral.add(new BoolSetting.Builder()
            .name("TP forward")
            .description("Teleports you double your radius forward after voiding to aid in voiding a perfect strip.")
            .defaultValue(false)
            .build()
    );
    public final Setting<Boolean> tgl = sgGeneral.add(new BoolSetting.Builder()
            .name("Toggle off after TP forward")
            .description("Turn module off after TP, or not.")
            .defaultValue(true)
            .visible(() -> tpfwd.get())
            .build()
    );

    public VoiderPlus() {
        super(Trouser.Main, "voider+", "Runs /fill on the world from the top down");
    }

    int i = maxheight.get();
    private int passes=0;
    private int TPs=0;
    private int pX;
    private int pY;
    private int pZ;
    private int sX;
    private int sY;
    private int sZ;
    @EventHandler
    private void onScreenOpen(OpenScreenEvent event) {
        if (event.screen instanceof DisconnectedScreen) {
            toggle();
        }
        if (event.screen instanceof DeathScreen) {
            toggle();
        }
    }
    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        toggle();
    }
    @Override
    public void onActivate() {
        i = maxheight.get();
        passes=0;
        TPs=0;
        sX=mc.player.getBlockPos().getX();
        sY=mc.player.getBlockPos().getY();
        sZ=mc.player.getBlockPos().getZ();
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (!(mc.player.hasPermissionLevel(4))) {
            toggle();
            error("Must have OP");
        }
                if (!threebythree.get() && !tpfwd.get()){
                ChatUtils.sendPlayerMsg("/fill " + (sX - radius.get()) + " " + i +" "+ (sZ - radius.get()) +" "+ (sX + radius.get()) + " " + i +" "+ (sZ + radius.get()) + " "+block);
                    i--;
                    if (i<=minheight.get()){
                        i=maxheight.get();
                        toggle();
                    }
                }  else if (!threebythree.get() &&tpfwd.get()){
                    if (i>= maxheight.get()){
                        sX=mc.player.getBlockPos().getX();
                        sY=mc.player.getBlockPos().getY();
                        sZ=mc.player.getBlockPos().getZ();
                    }
                    ChatUtils.sendPlayerMsg("/fill " + (sX - radius.get()) + " " + i +" "+ (sZ - radius.get()) +" "+ (sX + radius.get()) + " " + i +" "+ (sZ + radius.get()) + " "+block);
                    i--;
                    if (i<=minheight.get()){
                        switch (mc.player.getMovementDirection()){
                            case EAST -> {
                                ChatUtils.sendPlayerMsg("/tp "+(sX+(radius.get()*2))+" "+sY+" "+sZ);
                            }
                            case WEST ->  {
                                ChatUtils.sendPlayerMsg("/tp "+(sX+(-(radius.get()*2)))+" "+sY+" "+sZ);
                            }
                            case NORTH ->  {
                                ChatUtils.sendPlayerMsg("/tp "+sX+" "+sY+" "+(sZ+(-(radius.get()*2))));
                            }
                            case SOUTH ->  {
                                ChatUtils.sendPlayerMsg("/tp "+sX+" "+sY+" "+(sZ+(radius.get()*2)));
                            }
                        }
                        i=maxheight.get();
                        if (tgl.get()) toggle();
                    }
                } else if (threebythree.get() && !tpfwd.get()){
                    if (i<=maxheight.get() && passes==0 && TPs==0){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=1;
                        }
                    } else if (i==maxheight.get()+1 && passes == 1){
                        ChatUtils.sendPlayerMsg("/tp "+(sX+(radius.get()*2))+" "+sY+" "+sZ);
                        TPs=1;
                        i--;
                    } else if (i<=maxheight.get() && passes == 1 && TPs==1){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=2;
                        }
                    } else if (i==maxheight.get()+1 && passes == 2){
                        ChatUtils.sendPlayerMsg("/tp "+(sX+(radius.get()*2))+" "+sY+" "+(sZ+(-(radius.get()*2))));
                        TPs=2;
                        i--;
                    } else if (i<=maxheight.get() && passes == 2 && TPs==2){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=3;
                        }
                    } else if (i==maxheight.get()+1 && passes == 3){
                        ChatUtils.sendPlayerMsg("/tp "+sX+" "+sY+" "+(sZ+(-(radius.get()*2))));
                        TPs=3;
                        i--;
                    } else if (i<=maxheight.get() && passes == 3 && TPs==3){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=4;
                        }
                    } else if (i==maxheight.get()+1 && passes == 4){
                        ChatUtils.sendPlayerMsg("/tp "+(sX+(-(radius.get()*2)))+" "+sY+" "+(sZ+(-(radius.get()*2))));
                        TPs=4;
                        i--;
                    } else if (i<=maxheight.get() && passes == 4 && TPs==4){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=5;
                        }
                    } else if (i==maxheight.get()+1 && passes == 5){
                        ChatUtils.sendPlayerMsg("/tp "+(sX+(-(radius.get()*2)))+" "+sY+" "+sZ);
                        TPs=5;
                        i--;
                    } else if (i<=maxheight.get() && passes == 5 && TPs==5){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=6;
                        }
                    } else if (i==maxheight.get()+1 && passes == 6){
                        ChatUtils.sendPlayerMsg("/tp "+(sX+(-(radius.get()*2)))+" "+sY+" "+(sZ+(radius.get()*2)));
                        TPs=6;
                        i--;
                    } else if (i<=maxheight.get() && passes == 6 && TPs==6){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=7;
                        }
                    } else if (i==maxheight.get()+1 && passes == 7){
                        ChatUtils.sendPlayerMsg("/tp "+sX+" "+sY+" "+(sZ+(radius.get()*2)));
                        TPs=7;
                        i--;
                    } else if (i<=maxheight.get() && passes == 7 && TPs==7){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=8;
                        }
                    } else if (i==maxheight.get()+1 && passes == 8){
                        ChatUtils.sendPlayerMsg("/tp "+(sX+(radius.get()*2))+" "+sY+" "+(sZ+(radius.get()*2)));
                        TPs=8;
                        i--;
                    } else if (i<=maxheight.get() && passes == 8 && TPs==8){
                        i--;
                        pX=mc.player.getBlockPos().getX();
                        pY=mc.player.getBlockPos().getY();
                        pZ=mc.player.getBlockPos().getZ();
                        ChatUtils.sendPlayerMsg("/fill " + (pX - radius.get()) + " " + i +" "+ (pZ - radius.get()) +" "+ (pX + radius.get()) + " " + i +" "+ (pZ + radius.get()) + " "+block);
                        if (i<=minheight.get()){
                            i=maxheight.get()+1;
                            passes=9;
                        }
                    } else if (i==maxheight.get()+1 && passes >= 9){
                        ChatUtils.sendPlayerMsg("/tp "+sX+" "+sY+" "+sZ);
                        i = maxheight.get();
                        passes=0;
                        TPs=0;
                        toggle();
                    }
                }
                else if (threebythree.get() && tpfwd.get()){
                    error("Do Not use TPforward with VoiderBot.");
                    toggle();
                }
            }
}
