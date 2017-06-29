VERSION=`cat VERSION`
echo "VERSION: "$VERSION

CGO_ENABLED=0 go build
mv main hora
sudo docker build -t timz/rsshora .
sudo docker tag timz/rsshora timz/rsshora:$VERSION
sudo docker push timz/rsshora

