currentDir=`pwd`
input="$@"
cd $HOME/.cliffhanger

if [ ! -f $@ ]; then
    input=$currentDir/"$@"
fi

java -cp $HOME/.cliffhanger/cliffhanger-*.jar com.castronu.cliffangher.App $input

trap EXIT
cd $currentDir


